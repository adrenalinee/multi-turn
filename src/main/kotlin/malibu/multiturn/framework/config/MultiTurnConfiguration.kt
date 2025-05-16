package malibu.multiturn.framework.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.NamedType
import malibu.multiturn.framework.BehaviorRegistry
import malibu.multiturn.framework.exception.MultiTurnModuleConfigurerNotFoundException
import malibu.multiturn.framework.expression.ExpressionParser
import malibu.multiturn.framework.expression.SpelParser
import malibu.multiturn.model.BotScenario
import malibu.multiturn.model.MultiTurnModule
import malibu.multiturn.module.core.CoreModule
import malibu.multiturn.module.core.CoreModuleConfiguration
import mu.KotlinLogging
import kotlin.collections.plus
import kotlin.reflect.KClass

class MultiTurnConfiguration(
    /**
     * bot template 별로 하나씩 expression parser 를 생성하기 위해 생성자 함수형태로 입력받는다.
     * bot template 별로 expression variable 이 다르기때문에 parser 를 공유할 수 없어서 각각 만든다.
     */
    private val expressionParserCreator: () -> ExpressionParser = { SpelParser() } //spel (spring) 을 기본으로 사용
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 사용가능한 전체 모듈들
     */
    private val multiTurnModuleConfigurers: MutableMap<String, MultiTurnModuleConfigurer<MultiTurnModule>> = mutableMapOf()

    private val mappingFunctions: MutableList<MappingFunction> = mutableListOf()

    /**
     * 초기화 여부.
     * 초기화는 initialize() 호출로 이루어집니다.
     * 초기화는 한번만 할 수 있습니다.
     */
    private var isInit: Boolean = false
        get(): Boolean {
            return field
        }

    init {
        registerModule(CoreModule.Companion.TYPE, CoreModuleConfiguration())//기본 모듈 추가.
    }

    fun initialize(om: ObjectMapper) {
        if (logger.isInfoEnabled) {
            logger.info { "start" }
        }

        om.setSerializationInclusion(JsonInclude.Include.NON_NULL) //null 의 경우에는 json 으로 변환하지 않는다. 의미없는 데이터 통신을 줄일 수 있다.
        om.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE) //중요!!!! sub type이 정의되지 않은 타입일 경우에도 에러가 발생하지 않는다. 에러가 발생되면 intent request spec 변경에 의해서 운영중인 da 가 에러를 발생시킬 수 있다.
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        om.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL) // enum 으로 정의되지 않은 값인 경우 null로 처리하기 위해 옵션 활성화
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS) //비어 있는 오브젝트를 파싱할때 에러가 나는 상황을 막을 수 있다.

        mappingFunctions.forEach { func ->
            func.mapping(om)
        }

        multiTurnModuleConfigurers.forEach { (type, moduleConfig) ->
            if (logger.isDebugEnabled) {
                logger.debug { "- $type" }
            }

            moduleConfig.initialize()

            moduleConfig.mappingFunctions.forEach { func ->
                func.mapping(om)
            }
        }


        isInit = true
    }

    internal fun generateBehaviorRegistry(botScenario: BotScenario): BehaviorRegistry {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        if (isInit.not()) {
            throw RuntimeException("multiTurnConfiguration 이 초기화 되지 않았습니다. module resister 후에 initialize() 를 호출해야 합니다.")
        }

        val modules = botScenario.spec.getTopics()
            .flatMap { topic -> topic.getModules() }
            .let { modules -> modules + CoreModule() }
            .distinctBy { module -> module.type }

        //botScenario 하나당 하나의 expressionParser 를 가짐.
        //botScenario 에 등록된 모듈들이 다르기 때문에 expressionParser 에 등록된 variable 들도 같지 않음.
        val behaviorRegistry = BehaviorRegistry(expressionParserCreator())
            .also { behaviorRegistry ->
                modules.forEach { module ->
                    if (logger.isDebugEnabled) {
                        logger.debug { "- ${module.type}, desc: ${module.description?: ""}" }
                    }

                    val moduleConfig = multiTurnModuleConfigurers.get(module.type)
                        ?: throw MultiTurnModuleConfigurerNotFoundException(module.type)

                    moduleConfig.apply(module, botScenario.spec, behaviorRegistry)

                    moduleConfig.behaviorFunctions.forEach { func ->
                        func.register(behaviorRegistry)
                    }

                    moduleConfig.expressionVariableFunctions.forEach { func ->
                        func.register(behaviorRegistry.expressionParser)
                    }
                }
            }

        return behaviorRegistry
    }

    inline fun <reified T: MultiTurnModule> registerModule(type: String, moduleConfig: MultiTurnModuleConfigurer<T>) {
        @Suppress("UNCHECKED_CAST")
        registerModule(type, T::class as KClass<MultiTurnModule>, moduleConfig as MultiTurnModuleConfigurer<MultiTurnModule>)
    }

    fun registerModule(type: String, moduleClass: KClass<MultiTurnModule>, moduleConfig: MultiTurnModuleConfigurer<MultiTurnModule>) {
        if (isInit) {
            throw RuntimeException("multiTurnConfiguration 이 이미 초기화 되었습니다. registerModule() 은 초기화 전에 실행되어야 합니다. type: $type")
        }

        if (multiTurnModuleConfigurers.contains(type)) {
            logger.info { "이미 등록된 모듈입니다. moduleConfig: $moduleConfig" }
        } else {
            multiTurnModuleConfigurers.put(type, moduleConfig)
            mappingFunctions.add(object: MappingFunction {
                override fun mapping(om: ObjectMapper) {
                    om.registerSubtypes(NamedType(moduleClass.java, type))
                }
            })
        }
    }
}
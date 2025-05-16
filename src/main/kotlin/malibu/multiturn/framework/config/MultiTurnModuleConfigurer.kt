package malibu.multiturn.framework.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.NamedType
import malibu.multiturn.framework.*
import malibu.multiturn.model.Action
import malibu.multiturn.model.Argument
import malibu.multiturn.model.BotScenarioSpec
import malibu.multiturn.model.MultiTurnModule
import kotlin.reflect.KClass

/**
 * multiTurn 기능 확장을 목적으로하는 모듈을 설정할 수 있게 해줍니다.
 */
abstract class MultiTurnModuleConfigurer<in M: MultiTurnModule> {
    internal val expressionVariableFunctions = mutableListOf<ExpressionVariableFunc>()

    internal val mappingFunctions = mutableListOf<MappingFunction>()

    internal val behaviorFunctions = mutableListOf<BehaviorFunc>()

    /**
     * multiTurnConfiguration 이 최초실행될때 한번만 실행된다.
     */
    abstract fun initialize()

    /**
     * loadModule()
     * 개별 module 을 초기화 할때마다 실행된다.
     */
    abstract fun apply(
        module: M,
        botScenarioSpec: BotScenarioSpec,
        behaviorRegistry: BehaviorRegistry,
    )


    protected inline fun <reified T: Action> register(type: String, actionBehavior: ActionBehavior<T>) {
        register(type, T::class, actionBehavior)
    }

    protected inline fun <reified T: Argument> register(type: String, argumentBehavior: ArgumentBehavior<T>) {
        register(type, T::class, argumentBehavior)
    }

    protected fun <T: Action> register(
        type: String,
        actionClass: KClass<T>,
        actionBehavior: ActionBehavior<T>
    ) {
        mappingFunctions.add(object: MappingFunction {
            override fun mapping(om: ObjectMapper) {
                om.registerSubtypes(NamedType(actionClass.java, type))
            }
        })

        behaviorFunctions.add(
            object : BehaviorFunc {
                override fun register(behaviorRegistry: BehaviorRegistry) {
                    behaviorRegistry.register(type, actionBehavior)
                }
            }
        )
    }

    protected fun <T: Argument> register(
        type: String,
        argumentClass: KClass<T>,
        argumentBehavior: ArgumentBehavior<T>
    ) {
        mappingFunctions.add(object: MappingFunction {
            override fun mapping(om: ObjectMapper) {
                om.registerSubtypes(NamedType(argumentClass.java, type))
            }
        })

        behaviorFunctions.add(
            object : BehaviorFunc {
                override fun register(behaviorRegistry: BehaviorRegistry) {
                    behaviorRegistry.register(type, argumentBehavior)
                }
            }
        )
    }

    protected fun register(intendListener: IntendListener) {
        behaviorFunctions.add(
            object : BehaviorFunc {
                override fun register(behaviorRegistry: BehaviorRegistry) {
                    behaviorRegistry.registerFirst(intendListener)
                }
            }
        )
    }

    protected fun registerLastOrder(intendListener: IntendListener) {
        behaviorFunctions.add(
            object : BehaviorFunc {
                override fun register(behaviorRegistry: BehaviorRegistry) {
                    behaviorRegistry.registerLast(intendListener)
                }
            }
        )
    }

    protected fun registerExpressionVariable(name: String, value: Any) {
        expressionVariableFunctions.add(
            object : ExpressionVariableFunc {
                override fun register(expressionParser: ExpressionParser) {
                    expressionParser.registerVariable(name, value)
                }

            }
        )

    }
}
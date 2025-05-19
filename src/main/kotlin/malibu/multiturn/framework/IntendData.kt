package malibu.multiturn.framework

import malibu.multiturn.framework.expression.ExpressionRoot
import kotlin.reflect.KClass

class IntendData(
    val multiTurnReq: MultiTurnReq,
    val behaviorRegistry: BehaviorRegistry,
) {
    private val arguments: MutableMap<String, Any?> = mutableMapOf()

    /**
     * argument 로딩이 끝났는지 여부.
     */
    var finishIntendArgumentLoad: Boolean = false
        internal set

    /**
     * 표현식에서 사용할 root object.
     */
    private lateinit var expressionRoot: ExpressionRoot

    internal fun putArgument(argumentName: String, argumentValue: Any?) {
        if (arguments.containsKey(argumentName)) {
            throw RuntimeException("argument key: $argumentName is already exists.")
        }
        arguments.put(argumentName, argumentValue)
    }

    fun getArguments(): Map<String, Any?> {
        return arguments.toMap()
    }

    /*
     * @param expression - 표현식. 현재 spel 기반
     * @param desiredResultType - 표현식이 응답할 것으로 예상하는 타입.
     * @param temporaryVariables 지속적이지 않고 이번 한번의 표현식 파싱에서만 사용할 임시 변수
     */
    fun <T: Any> evaluate(
        expression: String,
        desiredResultType: KClass<T>,
        temporaryVariables: Map<String, Any> = emptyMap()
    ): T? {
        if (!::expressionRoot.isInitialized) { // 요청당 최초 evaluate 호출되었을때 생성한 값을 계속 사용하기 위해 이렇게 처리.
            expressionRoot = ExpressionRoot(
                //표현식에서 현재 attributes, arguments 와 daReq 에 접근가능
                req = multiTurnReq,
                args = arguments,
            )
        }

        return behaviorRegistry.expressionParser.evaluate(
            expression = expression,
            expressionRoot = expressionRoot,
            desiredResultType = desiredResultType,
            temporaryVariables = temporaryVariables
        )
    }

    /**
     *
     */
    fun resolvePlaceHolder(rawValue: String): String {
        return PlaceholderResolver.resolve(rawValue) { expression ->
            evaluate(expression, String::class)?: ""
        }
    }
}
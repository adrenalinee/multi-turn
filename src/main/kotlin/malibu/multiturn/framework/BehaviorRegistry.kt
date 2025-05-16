package malibu.multiturn.framework

import malibu.multiturn.framework.expression.ExpressionParser
import malibu.multiturn.framework.expression.ExpressionRoot
import malibu.multiturn.model.Action
import malibu.multiturn.model.Argument

class BehaviorRegistry(
    internal val expressionParser: ExpressionParser
) {
    private val actionBehaviors: MutableMap<String, ActionBehavior<out Action>> = mutableMapOf()
    private val argumentBehaviors: MutableMap<String, ArgumentBehavior<out Argument>> = mutableMapOf()
    private val intentListeners: MutableList<IntendListener> = mutableListOf()

    /**
     * 표현식에서 사용할 root object.
     */
    private lateinit var expressionRootObj: ExpressionRoot

//    /*
//     * @param expression - 표현식. 현재 spel 기반
//     * @param desiredResultType - 표현식이 응답할 것으로 예상하는 타입.
//     * @param temporaryVariables 지속적이지 않고 이번 한번의 표현식 파싱에서만 사용할 임시 변수
//     */
//    fun <T: Any> evaluate(
//        expression: String,
//        desiredResultType: KClass<T>,
//        temporaryVariables: Map<String, Any> = emptyMap()
//    ): T? {
//        if (!::expressionRootObj.isInitialized) { // 요청당 최초 evaluate 호출되었을때 생성한 값을 계속 사용하기 위해 이렇게 처리.
//            expressionRootObj = ExpressionRoot( //표현식에서 현재 attributes, arguments 와 daReq 에 접근가능
//                req = multiTurnReq,
//                args = argumentValueMap,
//            )
//        }
//
//        return behaviorsRegistry.expressionParser.evaluate(
//            expression,
//            expressionRootObj,
//            desiredResultType,
//            temporaryVariables
//        ).also { result ->
//            if (logger.isDebugEnabled) {
//                logger.debug { "expression: $expression" }
//                logger.debug { "expression result: $result" }
//            }
//        }
//    }

    fun register(type: String, actionRunner: ActionBehavior<*>) {
        actionBehaviors.put(type, actionRunner)
    }

    fun findActionBehaviorOrNull(type: String): ActionBehavior<*>? {
        return actionBehaviors.get(type)
    }

    fun register(type: String, argumentBehavior: ArgumentBehavior<*>) {
        argumentBehaviors.put(type, argumentBehavior)
    }

    fun findArgumentBehaviorOrNull(type: String): ArgumentBehavior<*>? {
        return argumentBehaviors.get(type)
    }

    fun registerLast(intendListener: IntendListener) {
        intentListeners.add(intendListener)
    }

    fun registerFirst(intendListener: IntendListener) {
        intentListeners.add(0, intendListener)
    }
}
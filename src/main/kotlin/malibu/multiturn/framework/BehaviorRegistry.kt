package malibu.multiturn.framework

import malibu.multiturn.model.Action
import malibu.multiturn.model.Argument

class BehaviorRegistry(
    internal val expressionParser: ExpressionParser
) {
    private val actionBehaviors: MutableMap<String, ActionBehavior<out Action>> = mutableMapOf()
    private val argumentBehaviors: MutableMap<String, ArgumentBehavior<out Argument>> = mutableMapOf()
    private val intentListeners: MutableList<IntendListener> = mutableListOf()

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
package malibu.multiturn.module.core

import malibu.multiturn.model.Action

class ExpressionAction(
    val expressions: List<String>
): Action(
    type = TYPE
) {
    companion object {
        val TYPE: String = ExpressionAction::class.java.simpleName
    }
}
package malibu.multiturn.module.core

import malibu.multiturn.model.Action
import malibu.multiturn.model.Param

data class RemoveConversationParamAction(
    val paramNames: List<String>
): Action(
    type = TYPE
) {
    companion object {
        val TYPE: String = RemoveConversationParamAction::class.java.simpleName
    }
}
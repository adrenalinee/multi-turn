package malibu.multiturn.module.core

import malibu.multiturn.model.Action
import malibu.multiturn.model.Param

data class AddConversationParamAction(
    val params: List<Param>
): Action(
    type = TYPE
) {
    companion object {
        val TYPE: String = AddConversationParamAction::class.java.simpleName
    }
}
package malibu.multiturn.module.core

import malibu.multiturn.model.Action
import malibu.multiturn.model.Param

data class AddInstantParamAction(
    val params: List<Param>
): Action(
    type = TYPE
) {
    companion object {
        val TYPE: String = AddInstantParamAction::class.java.simpleName
    }
}
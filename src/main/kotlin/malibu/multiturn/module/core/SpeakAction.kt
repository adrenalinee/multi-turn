package malibu.multiturn.module.core

import malibu.multiturn.model.Action

data class SpeakAction(
    val sentences: List<String>
): Action(
    type = TYPE
) {
    companion object {
        val TYPE: String = SpeakAction::class.java.simpleName
    }
}
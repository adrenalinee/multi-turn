package malibu.multiturn.framework

import malibu.multiturn.model.Action
import malibu.multiturn.model.Task

data class IntendResult(
    val selectedTask: Task,
    val executableActions: List<Action>,
    val multiTurnRes: MultiTurnRes,

    val appliedIntendListeners: List<IntendListener>,
)
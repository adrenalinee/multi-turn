package malibu.multiturn.framework

import malibu.multiturn.model.Action
import malibu.multiturn.model.Task

data class TaskResult(
    val selectedTask: Task,
    val executableActions: List<Action>,
)
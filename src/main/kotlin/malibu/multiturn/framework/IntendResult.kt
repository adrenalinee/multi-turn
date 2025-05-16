package malibu.multiturn.framework

data class IntendResult(
    val taskResult: TaskResult,
    val appliedIntendListeners: List<IntendListener>
)
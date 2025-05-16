package malibu.multiturn.framework

import malibu.multiturn.model.Action
import malibu.multiturn.model.Intend
import malibu.multiturn.model.Task
import malibu.multiturn.model.Topic
import malibu.multiturn.model.TopicState
import malibu.multiturn.model.dsl.intend

class ExecuteTrace(
    val multiTurnReq: MultiTurnReq
) {
    var selectedTopic: Topic? = null
    var selectedTopicState: TopicState? = null
    var isFallback: Boolean? = null
    var selectedIntend: Intend? = null
    var selectedTask: Task? = null
    var executedActions: List<Action> = emptyList()
    var appliedIntendListeners: List<IntendListener> = emptyList()
//    var multiTurnRes: MultiTurnRes? = null

    fun toIntendTrace(): IntendTrace {
        return IntendTrace(
            isFallback = isFallback,
            intend = selectedIntend?.name,
            task = selectedTask?.name,
            executedActions = executedActions.map { action -> action.toString() },
            appliedIntendListeners.map { listener -> listener.toString() }
        )
    }
}
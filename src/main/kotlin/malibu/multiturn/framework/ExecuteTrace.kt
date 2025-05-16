package malibu.multiturn.framework

import malibu.multiturn.model.Action
import malibu.multiturn.model.Intend
import malibu.multiturn.model.Task
import malibu.multiturn.model.Topic
import malibu.multiturn.model.TopicState

class ExecuteTrace {
    var selectedTopic: Topic? = null
    var selectedTopicState: TopicState? = null
    var selectedIntend: Intend? = null
    var selectedTask: Task? = null
    var executedActions: MutableList<Action> = mutableListOf()
    var appliedIntendListeners: MutableList<IntendListener> = mutableListOf()
}
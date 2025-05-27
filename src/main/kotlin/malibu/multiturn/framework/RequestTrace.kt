package malibu.multiturn.framework

class RequestTrace(
    val multiTurnReq: MultiTurnReq,
    val executedActions: List<String>? = null,
    val appliedIntendListeners: List<String>? = null,
) {
    var selectedTopic: String? = null
    var selectedTopicState: String? = null
//    var isFallback: Boolean? = null
    var selectedIntend: String? = null
    var selectedTask: String? = null
}
package malibu.multiturn.framework

import malibu.multiturn.model.Directive

data class MultiTurnRes(
    val requestId: String,
    val intent: String,
    val conversationId: String?,
    val botScenario: String,
    val scenarioVersion: String,
    val modelVersion: Int,
    val topic: String? = null,
    val topicState: String? = null,
) {
    private val conversationParams: MutableMap<String, Any> = mutableMapOf()
    private val requestParams: MutableMap<String, Any> = mutableMapOf()
    private val directives: MutableList<Directive> = mutableListOf()

    var intendTrace: IntendTrace? = null

    fun addDirective(directive: Directive) {
        directives.add(directive)
    }

    fun getDirectives(): List<Directive> {
        return directives.toList()
    }
}

data class IntendTrace(
//    var req: MultiTurnReq,
    var isFallback: Boolean? = null,
    var intend: String? = null,
    var task: String? = null,
    val executedActions: List<String>,
    val appliedListeners: List<String>,

) {
}
package malibu.multiturn.framework

data class MultiTurnRes(
    val requestId: String,
    val intent: String,
    val conversationId: String?,
    val botScenario: String,
    val scenarioVersion: String,
    val modelVersion: Int,
    val topic: String? = null,
    val topicState: String? = null,
    val intendTrace: IntendTrace? = null,
) {
    private val conversationParams: MutableMap<String, Any> = mutableMapOf()
    private val requestParams: MutableMap<String, Any> = mutableMapOf()
    private val directives: MutableList<Directive> = mutableListOf()


    fun addDirective(directive: Directive) {
        directives.add(directive)
    }

    fun getDirectives(): List<Directive> {
        return directives.toList()
    }
}

data class IntendTrace(
    var req: MultiTurnReq,
    var intend: String,
    var isFallback: Boolean,
    var task: String,
    val executedActions: MutableList<String>,
    val appliedListeners: MutableList<String>,

) {
}
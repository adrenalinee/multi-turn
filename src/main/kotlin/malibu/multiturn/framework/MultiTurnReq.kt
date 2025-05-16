package malibu.multiturn.framework

data class MultiTurnReq(
    val requestId: String,
    val intent: String,
    val conversationId: String? = null,
    val botScenario: String? = null,
    val scenarioVersion: String? = null,
    val modelVersion: Int? = null,
    val topic: String? = null,
    val topicState: String? = null,
    val conversationParams: Map<String, Any> = emptyMap(),
    val requestParams: Map<String, Any> = emptyMap(),
)

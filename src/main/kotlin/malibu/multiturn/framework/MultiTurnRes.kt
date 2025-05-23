package malibu.multiturn.framework

import com.fasterxml.jackson.annotation.JsonProperty
import malibu.multiturn.model.Directive

data class MultiTurnRes(
    val requestId: String,
    val intent: String,
    val conversationId: String?,
    val botScenario: String,
    val scenarioVersion: String,
    val modelVersion: Int,
) {

    @JsonProperty
    private val conversationParams: MutableMap<String, Any> = mutableMapOf()

    @JsonProperty
    private val instantParams: MutableMap<String, Any> = mutableMapOf()

    private val directives: MutableList<Directive> = mutableListOf()

    var nextTopic: String? = null

    var nextTopicState: String? = null

    var trace: IntendTrace? = null

    fun addDirective(directive: Directive) {
        directives.add(directive)
    }
    fun getDirectives(): List<Directive> {
        return directives.toList()
    }

    fun setConversationParam(name: String, value: Any, overwrite: Boolean = false) {
        if (overwrite && conversationParams.containsKey(name)) {
            throw RuntimeException("name: $name is already exists.")
        }
        conversationParams[name] = value
    }

    fun setAllConversationParams(params: Map<String, Any>) {
        conversationParams.putAll(params)
    }

    fun removeConversationParam(name: String): Boolean {
        return conversationParams.remove(name) != null
    }

    fun getConversationParam(name: String): Any? = conversationParams[name]

    fun setInstantParam(name: String, value: Any, overwrite: Boolean = false) {
        if (overwrite && instantParams.containsKey(name)) {
            throw RuntimeException("name: $name is already exists.")
        }
        instantParams[name] = value
    }

    fun getInstantParam(name: String): Any? = instantParams[name]

    fun setAllInstantParams(params: Map<String, Any>) {
        instantParams.putAll(params)
    }
}

data class IntendTrace(
    var isFallback: Boolean? = null,
    var intend: String? = null,
    var task: String? = null,
    val executedActions: List<String>,
    val appliedListeners: List<String>,

)
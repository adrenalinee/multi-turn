package malibu.multiturn.model

data class Topic(
    val name: String,
    val title: String? = null,
    val description: String? = null,
) {
    private val modules: MutableList<MultiTurnModule> = mutableListOf()

    private val states: MutableMap<String, TopicState> = mutableMapOf()

    var defaultStateName: String? = null
        private set
        get(): String? {
            if (field == null && states.size == 1) {
                field = states.values.first().name
            }

            return field
        }

    fun addModule(module: MultiTurnModule) {
        modules.add(module)
    }

    fun getModules(): List<MultiTurnModule> {
        return modules.toList()
    }

    fun addState(topicState: TopicState, default: Boolean = false) {
        states.put(topicState.name, topicState)
        if (default) {
            defaultStateName = topicState.name
        }
    }

    fun getStates(): List<TopicState> {
        return states.values.toList()
    }

    fun getState(stateName: String): TopicState? {
        return states.get(stateName)
    }
}

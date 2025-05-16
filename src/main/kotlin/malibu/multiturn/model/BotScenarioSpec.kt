package malibu.multiturn.model

data class BotScenarioSpec(
    val description: String? = null,
) {
    var defaultTopicName: String? = null
        private set
        get(): String? {
            if (field == null && topics.size == 1) {
                field = topics.values.first().name
            }

            return field
        }

    private val topics: MutableMap<String, Topic> = mutableMapOf()

    fun addTopic(topic: Topic, default: Boolean = false) {
        topics.put(topic.name, topic)
        if (default) {
            defaultTopicName = topic.name
        }
    }

    fun getTopics(): List<Topic> {
        return topics.values.toList()
    }

    fun getTopic(topicName: String): Topic? {
        return topics.get(topicName)
    }
}

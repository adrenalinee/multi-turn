package malibu.multiturn.model

data class TopicState(
    val name: String,
    val description: String? = null,
) {
    private val headIntends: MutableList<Intend> = mutableListOf()

    private val fallbackIntends: MutableList<Intend> = mutableListOf()

    private val headIntendsByIntentName: MutableMap<String, Intend> = mutableMapOf()

    private val fallbackIntendsByIntentName: MutableMap<String, Intend> = mutableMapOf()


    fun addHeadIntend(intend: Intend) {
        headIntends.add(intend)
        intend.getIntentNames().forEach { intentName ->
            if (headIntendsByIntentName.contains(intentName)) {
                throw RuntimeException("duplicated intent name: $intentName")
            }
            headIntendsByIntentName.put(intentName, intend)
        }
    }

    fun getHeadIntends(): List<Intend> {
        return headIntends.toList()
    }

    fun findHeadIntendByIntentName(intentName: String): Intend? {
        return headIntendsByIntentName.get(intentName)
    }

    fun addFallbackIntend(intend: Intend) {
        fallbackIntends.add(intend)
        intend.getIntentNames().forEach { intentName ->
            if (headIntendsByIntentName.contains(intentName)) {
                throw RuntimeException("duplicated intent name: $intentName")
            }
            fallbackIntendsByIntentName.put(intentName, intend)
        }
    }

    fun getFallbackIntends(): List<Intend> {
        return fallbackIntends.toList()
    }

    fun findFallbackIntendByIntentName(intentName: String): Intend? {
        return fallbackIntendsByIntentName.get(intentName)
    }
}

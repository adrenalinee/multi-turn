package malibu.multiturn.model

data class BotScenario(
    val name: String,
    val title: String? = null,
    val description: String? = null,
    val modelVersion: Int = 1,
    val scenarioVersion: String,
    val spec: BotScenarioSpec,
)

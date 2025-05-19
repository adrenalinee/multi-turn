package malibu.multiturn

import malibu.multiturn.model.BotScenario
import malibu.multiturn.model.Param
import malibu.multiturn.model.dsl.*
import malibu.multiturn.module.core.AddConversationParamAction
import malibu.multiturn.module.core.AddInstantParamAction
import malibu.multiturn.module.core.SpeakAction

object TestBotScenarios {
    fun generate(): BotScenario {
        return botScenario(
            name = "sc_test",
            scenarioVersion = "v1",
            spec = botScenarioSpec {
                addTopic(topic(
                    name = "to_test",
                ) {
                    addState(state(
                        name = "s_test",
                    ) {
                        addHeadIntend(intend(
                            name = "i_test",
                            triggerExpression = "true"
                        ) {
                            addTask(task(
                                name = "ta_test",
                                triggerExpression = "true"
                            ) {
                                addAction(SpeakAction(
                                    sentences = listOf("hello!")
                                ))
                                addAction(AddConversationParamAction(
                                    params = listOf(
                                        Param(name = "simple-text-param", value = "simple-text"),
                                        Param(name = "expression-param", value = "1 + 2 = {{1 + 2}}")
                                    )
                                ))
                                addAction(AddInstantParamAction(
                                    params = listOf(
                                        Param(name = "simple-text-param", value = "simple-text"),
                                        Param(name = "expression-param", value = "intent={{req.intent}}"),
                                    )
                                ))
                            })
                        })
                    })
                })
            }
        ) {}
    }
}
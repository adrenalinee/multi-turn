package malibu.multiturn

import malibu.multiturn.model.BotScenario
import malibu.multiturn.model.dsl.botScenario
import malibu.multiturn.model.dsl.botScenarioSpec
import malibu.multiturn.model.dsl.intend
import malibu.multiturn.model.dsl.state
import malibu.multiturn.model.dsl.task
import malibu.multiturn.model.dsl.topic
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
                            })
                        })
                    })
                })
            }
        ) {}
    }
}
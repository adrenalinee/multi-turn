package malibu.multiturn.model.dsl

import malibu.multiturn.model.BotScenario
import malibu.multiturn.model.BotScenarioSpec
import malibu.multiturn.model.Intend
import malibu.multiturn.model.Topic
import malibu.multiturn.model.TopicState
import malibu.multiturn.model.Task

fun botScenario(
    name: String,
    scenarioVersion: String,
    spec: BotScenarioSpec,
    block: BotScenario.() -> Unit
): BotScenario {
    return BotScenario(
        name = name,
        scenarioVersion = scenarioVersion,
        spec = spec,
    ).apply(block)
}

fun botScenarioSpec(

    block: BotScenarioSpec.() -> Unit
): BotScenarioSpec {
    return BotScenarioSpec(

    ).apply(block)
}

fun topic(
    name: String,
    block: Topic.() -> Unit
): Topic {
    return Topic(
        name = name,
    ).apply(block)
}

fun state(
    name: String,
    block: TopicState.() -> Unit
): TopicState {
    return TopicState(
        name = name,
    ).apply(block)
}

fun intend(
    name: String,
    triggerExpression: String? = null,
    block: Intend.() -> Unit
): Intend {
    return Intend(
        name = name,
        triggerExpression = triggerExpression,
    ).apply(block)
}

fun task(
    name: String,
    triggerExpression: String,
    block: Task.() -> Unit
): Task {
    return Task(
        name = name,
        triggerExpression = triggerExpression,
    ).apply(block)
}
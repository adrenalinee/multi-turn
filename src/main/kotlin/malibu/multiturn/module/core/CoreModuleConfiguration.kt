package malibu.multiturn.module.core

import malibu.multiturn.framework.BehaviorRegistry
import malibu.multiturn.framework.config.MultiTurnModuleConfigurer
import malibu.multiturn.model.BotScenarioSpec
import malibu.multiturn.module.core.behavior.SpeakActionBehavior

class CoreModuleConfiguration: MultiTurnModuleConfigurer<CoreModule>() {
    override fun initialize() {
        register(SpeakAction.TYPE, SpeakActionBehavior())
    }

    override fun apply(
        module: CoreModule,
        botScenarioSpec: BotScenarioSpec,
        behaviorRegistry: BehaviorRegistry
    ) {
        //nothing
    }
}
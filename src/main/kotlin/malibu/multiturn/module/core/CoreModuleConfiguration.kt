package malibu.multiturn.module.core

import malibu.multiturn.framework.BehaviorRegistry
import malibu.multiturn.framework.config.MultiTurnModuleConfigurer
import malibu.multiturn.model.BotScenarioSpec
import malibu.multiturn.module.core.behavior.AddConversationParamActionBehavior
import malibu.multiturn.module.core.behavior.AddInstantParamActionBehavior
import malibu.multiturn.module.core.behavior.ExpressionActionBehavior
import malibu.multiturn.module.core.behavior.ExpressionArgumentBehavior
import malibu.multiturn.module.core.behavior.RemoveConversationParamActionBehavior
import malibu.multiturn.module.core.behavior.SpeakActionBehavior
import malibu.multiturn.module.core.behavior.TransferInstantParamActionBehavior

class CoreModuleConfiguration: MultiTurnModuleConfigurer<CoreModule>() {
    override fun initialize() {
        register(ExpressionAction.TYPE, ExpressionActionBehavior())
        register(SpeakAction.TYPE, SpeakActionBehavior())
        register(AddConversationParamAction.TYPE, AddConversationParamActionBehavior())
        register(AddInstantParamAction.TYPE, AddInstantParamActionBehavior())
        register(RemoveConversationParamAction.TYPE, RemoveConversationParamActionBehavior())
        register(TransferInstantParamAction.TYPE, TransferInstantParamActionBehavior())

        register(ExpressionArgument.TYPE, ExpressionArgumentBehavior())
    }

    override fun apply(
        module: CoreModule,
        botScenarioSpec: BotScenarioSpec,
        behaviorRegistry: BehaviorRegistry
    ) {
        //nothing
    }
}
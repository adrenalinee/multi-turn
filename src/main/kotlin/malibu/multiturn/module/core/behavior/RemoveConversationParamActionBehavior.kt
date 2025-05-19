package malibu.multiturn.module.core.behavior

import malibu.multiturn.framework.ActionBehavior
import malibu.multiturn.framework.IntendData
import malibu.multiturn.framework.MultiTurnRes
import malibu.multiturn.module.core.AddConversationParamAction
import malibu.multiturn.module.core.RemoveConversationParamAction
import mu.KotlinLogging
import reactor.core.publisher.Mono

class RemoveConversationParamActionBehavior(

): ActionBehavior<RemoveConversationParamAction>() {
    private val logger = KotlinLogging.logger {  }

    override fun run(
        action: RemoveConversationParamAction,
        intendData: IntendData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void> {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        action.paramNames.forEach { paramName ->
            multiTurnRes.removeConversationParam(paramName)
        }

        return Mono.empty()
    }
}
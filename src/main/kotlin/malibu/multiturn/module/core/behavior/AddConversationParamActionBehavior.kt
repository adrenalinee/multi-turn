package malibu.multiturn.module.core.behavior

import malibu.multiturn.framework.ActionBehavior
import malibu.multiturn.framework.RequestData
import malibu.multiturn.framework.MultiTurnRes
import malibu.multiturn.module.core.AddConversationParamAction
import mu.KotlinLogging
import reactor.core.publisher.Mono

class AddConversationParamActionBehavior(

): ActionBehavior<AddConversationParamAction>() {
    private val logger = KotlinLogging.logger {  }

    override fun run(
        action: AddConversationParamAction,
        requestData: RequestData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void> {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        action.params.forEach { param ->
            multiTurnRes.setConversationParam(
                name = param.name,
                value = requestData.resolvePlaceHolder(param.value)
            )
        }

        return Mono.empty()
    }
}
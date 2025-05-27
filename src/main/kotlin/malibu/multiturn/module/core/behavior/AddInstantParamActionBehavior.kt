package malibu.multiturn.module.core.behavior

import malibu.multiturn.framework.ActionBehavior
import malibu.multiturn.framework.RequestData
import malibu.multiturn.framework.MultiTurnRes
import malibu.multiturn.module.core.AddInstantParamAction
import mu.KotlinLogging
import reactor.core.publisher.Mono

class AddInstantParamActionBehavior(

): ActionBehavior<AddInstantParamAction>() {
    private val logger = KotlinLogging.logger {  }

    override fun run(
        action: AddInstantParamAction,
        requestData: RequestData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void> {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        action.params.forEach { param ->
            multiTurnRes.setInstantParam(
                name = param.name,
                value = requestData.resolvePlaceHolder(param.value)
            )
        }

        return Mono.empty()
    }
}
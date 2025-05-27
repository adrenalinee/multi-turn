package malibu.multiturn.module.core.behavior

import malibu.multiturn.framework.ActionBehavior
import malibu.multiturn.framework.RequestData
import malibu.multiturn.framework.MultiTurnRes
import malibu.multiturn.module.core.TransferInstantParamAction
import mu.KotlinLogging
import reactor.core.publisher.Mono

class TransferInstantParamActionBehavior(

): ActionBehavior<TransferInstantParamAction>() {
    private val logger = KotlinLogging.logger { }

    override fun run(
        action: TransferInstantParamAction,
        requestData: RequestData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void> {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        multiTurnRes.setAllInstantParams(requestData.multiTurnReq.instantParams)

        return Mono.empty()
    }
}
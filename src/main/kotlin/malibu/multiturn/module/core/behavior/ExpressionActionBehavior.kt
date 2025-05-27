package malibu.multiturn.module.core.behavior

import malibu.multiturn.framework.ActionBehavior
import malibu.multiturn.framework.RequestData
import malibu.multiturn.framework.MultiTurnRes
import malibu.multiturn.module.core.ExpressionAction
import mu.KotlinLogging
import reactor.core.publisher.Mono

class ExpressionActionBehavior(

): ActionBehavior<ExpressionAction>() {
    private val logger = KotlinLogging.logger {  }

    override fun run(
        action: ExpressionAction,
        requestData: RequestData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void> {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        action.expressions.forEach { expression ->
            requestData.evaluate(
                expression = expression,
                desiredResultType = Any::class
            )
        }

        return Mono.empty()
    }
}
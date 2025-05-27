package malibu.multiturn.module.core.behavior

import malibu.multiturn.framework.ArgumentBehavior
import malibu.multiturn.framework.RequestData
import malibu.multiturn.module.core.ExpressionArgument
import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class ExpressionArgumentBehavior(

): ArgumentBehavior<ExpressionArgument>() {
    private val logger = KotlinLogging.logger {  }

    override fun retrieve(
        argument: ExpressionArgument,
        requestData: RequestData
    ): Mono<out Any> {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        val result = requestData.evaluate(argument.expression, Any::class)

        return when (result) {
            is Mono<*> -> result
            is Flux<*> -> result.collectList()
            else -> result.toMono()
        }
    }
}
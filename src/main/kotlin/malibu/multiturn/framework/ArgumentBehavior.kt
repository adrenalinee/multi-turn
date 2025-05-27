package malibu.multiturn.framework

import malibu.multiturn.model.Argument
import reactor.core.publisher.Mono

abstract class ArgumentBehavior<T: Argument> {
    fun behave(
        argument: Argument,
        requestData: RequestData,
    ): Mono<out Any> {
        @Suppress("UNCHECKED_CAST")
        return retrieve(argument as T, requestData)
    }

    /**
     * argument value 를 가져온다.
     */
    protected abstract fun retrieve(
        argument: T,
        requestData: RequestData,
    ): Mono<out Any>
}
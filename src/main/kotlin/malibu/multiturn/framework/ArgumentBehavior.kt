package malibu.multiturn.framework

import malibu.multiturn.model.Argument
import reactor.core.publisher.Mono

abstract class ArgumentBehavior<T: Argument> {
    fun behave(
        argument: Argument,
        intendData: IntendData,
    ): Mono<out Any> {
        @Suppress("UNCHECKED_CAST")
        return retrieve(argument as T, intendData)
    }

    /**
     * argument value 를 가져온다.
     */
    protected abstract fun retrieve(
        argument: T,
        intendData: IntendData,
    ): Mono<out Any>
}
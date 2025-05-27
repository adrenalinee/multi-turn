package malibu.multiturn.framework

import malibu.multiturn.model.Action
import reactor.core.publisher.Mono

abstract class ActionBehavior<T: Action> {
    fun behave(
        action: Action,
        requestData: RequestData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void> {
        @Suppress("UNCHECKED_CAST")
        return run(action as T, requestData, multiTurnRes)
    }

    /**
     * action 을 실행한다.
     */
    protected abstract fun run(
        action: T,
        requestData: RequestData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void>
}
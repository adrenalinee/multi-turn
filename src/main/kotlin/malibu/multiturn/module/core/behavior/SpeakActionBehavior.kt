package malibu.multiturn.module.core.behavior

import malibu.multiturn.framework.ActionBehavior
import malibu.multiturn.framework.RequestData
import malibu.multiturn.framework.MultiTurnRes
import malibu.multiturn.module.core.SpeakAction
import malibu.multiturn.module.core.directive.SpeakDirective
import mu.KotlinLogging
import reactor.core.publisher.Mono

class SpeakActionBehavior(
): ActionBehavior<SpeakAction>(){
    private val logger = KotlinLogging.logger {  }

    override fun run(
        action: SpeakAction,
        requestData: RequestData,
        multiTurnRes: MultiTurnRes
    ): Mono<Void> {
        if (logger.isDebugEnabled) {
            logger.debug { "start" }
        }

        multiTurnRes.addDirective(SpeakDirective(
            sentences = action.sentences.map { sentence ->
                requestData.resolvePlaceHolder(sentence)
            }
        ))

        return Mono.empty()
    }
}
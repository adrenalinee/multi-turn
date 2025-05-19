package malibu.multiturn.framework

import malibu.multiturn.framework.config.MultiTurnConfiguration
import malibu.multiturn.framework.exception.*
import malibu.multiturn.model.*
import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

class MultiTurnHandler(
    private val botScenario: BotScenario,
    configuration: MultiTurnConfiguration,
) {
    private val logger = KotlinLogging.logger {}

    private val behaviorRegistry = configuration.generateBehaviorRegistry(botScenario)

    private val support = MultiTurnHandlerSupport(botScenario, behaviorRegistry)

    /**
     *
     */
    fun handle(
        multiTurnReq: MultiTurnReq
    ): Mono<MultiTurnRes> {
        if (logger.isDebugEnabled) {
            logger.debug { "handle: $multiTurnReq" }
        }

        val executeTrace = ExecuteTrace(multiTurnReq)
        val (topic, topicState) = support.findTopicState(botScenario, multiTurnReq)

        executeTrace.selectedTopic = topic
        executeTrace.selectedTopicState = topicState

        val intendData = IntendData(
            multiTurnReq = multiTurnReq,
            behaviorRegistry = behaviorRegistry,
        )

        return topicState.findHeadIntendByIntentName(multiTurnReq.intent)
            .toMono()
            .switchIfEmpty {
                support.findIntendByTriggerExpression(
                    intends = topicState.getHeadIntends(),
                    intendData = intendData,
                )
            }
            .switchIfEmpty {
                topicState.findFallbackIntendByIntentName(multiTurnReq.intent)
                    .toMono()
                    .switchIfEmpty {
                        support.findIntendByTriggerExpression(
                            intends = topicState.getFallbackIntends(),
                            intendData = intendData,
                        )
                    }
            }
            .switchIfEmpty { Mono.error(IntendNotSelectedException(topicState)) }
            .flatMap { selectedIntend ->
                executeTrace.selectedIntend = selectedIntend

                val toApplyIntendListeners = riseBeforeTaskSelect(
                    behaviorRegistry.getIntentListeners(),
                    intendData
                ).cache()

                riseBeforeTasksRun(toApplyIntendListeners, intendData)
                    .flatMap { support.findTask(selectedIntend, intendData) }
                    .switchIfEmpty { Mono.error(TaskNotSelectedException(selectedIntend)) }
                    .flatMap { selectedTask ->
                        executeTrace.selectedTask = selectedTask
                        val multiTurnRes = support.createMultiTurnRes(multiTurnReq, topic, topicState)

                        support.executeActions(multiTurnRes, selectedTask, intendData)
                            .flatMap { executeActions ->
                                riseAfterTaskRun(toApplyIntendListeners, intendData).thenReturn(executeActions)
                            }
                            .map { executedActions ->
                                executeTrace.executedActions = executedActions

                                multiTurnRes.trace = executeTrace.toIntendTrace()
                                multiTurnRes
                            }
                    }
            }
            .switchIfEmpty { Mono.error(RuntimeException("intentResult 가 생성되지 않았습니다.")) }
    }

    /**
     * task select 하기 전에 실행되기 전에 발생하는 beforeTaskSelect 이벤트 실행
     * @return 등록된 전체 IntendListener 중에 이번 요청에서 처리할 listener 들을 골라서 리턴.
     */
    private fun riseBeforeTaskSelect(
        intendListeners: List<IntendListener>,
        intendData: IntendData,
    ): Flux<IntendListener> {
        return intendListeners
            .toFlux()
            .filterWhen { intendListener ->
                intendListener.beforeTaskSelect(intendData)
                    .defaultIfEmpty(true) // 값이 안넘어 오면 기본적으로 실행시킴..
                    .onErrorMap { ex -> BeforeTaskSelectRunException(intendListener, ex) }
            }
    }

    /**
     * task run 실행 직전에 발생하는 beforeTasksRun 이벤트 실행
     */
    private fun riseBeforeTasksRun(
        toApplyIntendListeners: Flux<IntendListener>,
        intendData: IntendData,
    ): Mono<List<Unit>> {
        return toApplyIntendListeners.flatMap { intendListener ->
            intendListener.beforeTasksRun(intendData)
                .onErrorMap { ex -> BeforeTasksRunException(intendListener, ex) }
        }.collectList()
    }

    /**
     * task run 실행 직후에 발생하는 afterTasksRun 이벤트 실행
     */
    private fun riseAfterTaskRun(
        toApplyIntentListeners: Flux<IntendListener>,
        intendData: IntendData,
//        taskResult: TaskResult
    ): Mono<List<Unit>> {
        return toApplyIntentListeners.collectList()
            .flatMapIterable { it.reversed() }
            .flatMap { intendListener ->
                intendListener.afterTasksRun(intendData/*, taskResult*/)
                    .onErrorMap { ex -> AfterTasksRunException(intendListener, ex) }
            }.collectList()
    }
}
package malibu.multiturn.framework

import malibu.multiturn.framework.config.MultiTurnConfiguration
import malibu.multiturn.framework.exception.*
import malibu.multiturn.model.BotScenario
import malibu.multiturn.model.Task
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
        multiTurnReq: MultiTurnReq,
        trace: Boolean = false
    ): Mono<MultiTurnRes> {
        if (logger.isDebugEnabled) {
            logger.debug { "handle: $multiTurnReq" }
        }

        val (topic, topicState) = support.findTopicState(botScenario, multiTurnReq)

        val requestData = RequestData(
            multiTurnReq = multiTurnReq,
            behaviorRegistry = behaviorRegistry,
            botScenario = botScenario,
            topic = topic,
            topicState = topicState,
        )

        return topicState.findHeadIntendByIntentName(multiTurnReq.intent)
            .toMono()
            .switchIfEmpty {
                support.findIntendByTriggerExpression(
                    intends = topicState.getHeadIntends(),
                    requestData = requestData,
                )
            }
            .switchIfEmpty {
                topicState.findFallbackIntendByIntentName(multiTurnReq.intent)
                    .toMono()
                    .switchIfEmpty {
                        support.findIntendByTriggerExpression(
                            intends = topicState.getFallbackIntends(),
                            requestData = requestData,
                        )
                    }
            }
            .switchIfEmpty { Mono.error(IntendNotSelectedException(topicState)) }
            .flatMap { selectedIntend ->
                requestData.selectedIntend = selectedIntend
                val toApplyIntendListeners = riseBeforeTaskSelect(
                    behaviorRegistry.getIntentListeners(),
                    requestData
                ).cache()

                toApplyIntendListeners.collectList()
                    .flatMap { appliedIntendListeners ->
                        requestData.appliedTaskListeners = appliedIntendListeners
                        riseBeforeTasksRun(toApplyIntendListeners, requestData)
                    }
                    .flatMap { support.findTask(selectedIntend, requestData) }
                    .switchIfEmpty { Mono.error(TaskNotSelectedException(selectedIntend)) }
                    .flatMap { selectedTask ->
                        requestData.selectedTask = selectedTask
                        val multiTurnRes = support.createMultiTurnRes(multiTurnReq/*, topic, topicState*/)

                        support.executeActions(multiTurnRes, selectedTask, requestData)
                            .flatMap { executeActions ->
                                riseAfterTaskRun(toApplyIntendListeners, requestData, selectedTask, multiTurnRes)
                                    .thenReturn(executeActions)
                            }
                            .map { executedActions ->
                                requestData.executedActions = executedActions
                                multiTurnRes
                            }
                            .doOnNext { multiTurnRes ->
                                support.postTasksRun(requestData, selectedTask, multiTurnRes)
                            }
                            .flatMap { multiTurnRes ->
                                riseAfterTasksRunCompletion(toApplyIntendListeners, requestData, selectedTask, multiTurnRes)
                                    .thenReturn(multiTurnRes)
                            }
                    }
            }
            .doOnNext { multiTurnRes ->
                if (trace) {
                    multiTurnRes.trace = requestData.createRequestTrace()
                }
            }
//            .onErrorMap(Exception::class.java) { ex ->
//
//            }
            .switchIfEmpty { Mono.error(RuntimeException("intentResult 가 생성되지 않았습니다.")) }
    }

    /**
     * task select 하기 전에 실행되기 전에 발생하는 beforeTaskSelect 이벤트 실행
     * @return 등록된 전체 IntendListener 중에 이번 요청에서 처리할 listener 들을 골라서 리턴.
     */
    private fun riseBeforeTaskSelect(
        taskListeners: List<TaskListener>,
        requestData: RequestData,
    ): Flux<TaskListener> {
        return taskListeners
            .toFlux()
            .filterWhen { intendListener ->
                intendListener.beforeTaskSelect(requestData)
                    .defaultIfEmpty(true) // 값이 안넘어 오면 기본적으로 실행시킴..
                    .onErrorMap { ex -> BeforeTaskSelectRunException(intendListener, ex) }
            }
    }

    /**
     * task run 실행 직전에 발생하는 beforeTasksRun 이벤트 실행
     */
    private fun riseBeforeTasksRun(
        toApplyTaskListeners: Flux<TaskListener>,
        requestData: RequestData,
    ): Mono<List<Unit>> {
        return toApplyTaskListeners.flatMap { intendListener ->
            intendListener.beforeTasksRun(requestData)
                .onErrorMap { ex -> BeforeTasksRunException(intendListener, ex) }
        }.collectList()
    }

    /**
     * task run 실행 직후에 발생하는 afterTasksRun 이벤트 실행
     */
    private fun riseAfterTaskRun(
        toApplyIntentListeners: Flux<TaskListener>,
        requestData: RequestData,
        selectedTask: Task,
        multiTurnRes: MultiTurnRes,
    ): Mono<List<Unit>> {
        return toApplyIntentListeners.collectList()
            .flatMapIterable { it.reversed() }
            .flatMap { intendListener ->
                intendListener.afterTasksRun(requestData, selectedTask, multiTurnRes)
                    .onErrorMap { ex -> AfterTasksRunException(intendListener, ex) }
            }.collectList()
    }

    private fun riseAfterTasksRunCompletion(
        toApplyIntentListeners: Flux<TaskListener>,
        requestData: RequestData,
        selectedTask: Task,
        multiTurnRes: MultiTurnRes,
    ): Mono<List<Unit>> {
        return toApplyIntentListeners.collectList()
            .flatMapIterable { it.reversed() }
            .flatMap { intendListener ->
                intendListener.afterTasksRunCompletion(requestData, selectedTask, multiTurnRes)
                    .onErrorMap { ex -> AfterTasksRunException(intendListener, ex) }
            }.collectList()
    }
}
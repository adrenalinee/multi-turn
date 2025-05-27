package malibu.multiturn.framework

import malibu.multiturn.framework.exception.ActionBehaviorNotFoundException
import malibu.multiturn.framework.exception.ActionBehaviorRunException
import malibu.multiturn.framework.exception.ArgumentBehaviorNotFoundException
import malibu.multiturn.framework.exception.ArgumentValueRequiredException
import malibu.multiturn.model.*
import mu.KotlinLogging
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

class MultiTurnHandlerSupport(
    private val botScenario: BotScenario,
    private val behaviorRegistry: BehaviorRegistry,
) {
    private val logger = KotlinLogging.logger {}

    internal fun createMultiTurnRes(
        multiTurnReq: MultiTurnReq,
    ): MultiTurnRes {
        return MultiTurnRes(
            requestId = multiTurnReq.requestId,
            conversationId = multiTurnReq.conversationId,
            intent = multiTurnReq.intent,
            botScenario = botScenario.name,
            scenarioVersion = botScenario.scenarioVersion,
            modelVersion = botScenario.modelVersion,
        ).also { multiTurnRes ->
            multiTurnRes.setAllConversationParams(multiTurnReq.conversationParams)
        }
    }

    /**
     *
     */
    internal fun postTasksRun(
        requestData: RequestData,
        selectedTask: Task,
        multiTurnRes: MultiTurnRes,
    ) {
        if (selectedTask.nextTopicStateUnset == true) {
            multiTurnRes.nextTopic = null
        } else {
            selectedTask.nextTopic
                ?.also { nextTopicName -> multiTurnRes.nextTopic = nextTopicName }
                ?: run {
                    multiTurnRes.nextTopic = requestData.topic.name
                }
        }

        if (selectedTask.nextTopicStateUnset == true) {
            multiTurnRes.nextTopicState = null
        } else {
            selectedTask.nextTopicState
                ?.also { nextTopicStateName -> multiTurnRes.nextTopicState = nextTopicStateName }
                ?: run {
                    multiTurnRes.nextTopicState = requestData.topicState.name
                }
        }
    }

    /**
     *
     */
    internal fun findTopicState(botScenario: BotScenario, multiTurnReq: MultiTurnReq): Pair<Topic, TopicState> {
        if (multiTurnReq.botScenario != null && multiTurnReq.botScenario != botScenario.name) {
            throw RuntimeException("처리할 수 있는 요청이 아닙니다. ${botScenario.name} 만 처리할 수 있습니다. request botScenario: ${multiTurnReq.botScenario}")
        }

        val topicName = multiTurnReq.topic?: botScenario.spec.defaultTopicName
        ?: throw RuntimeException("default topic not found.")
        val topic = botScenario.spec.getTopic(topicName)
            ?: throw RuntimeException("topic not found. topicName: $topicName")

        val topicStateName = multiTurnReq.topicState?: topic.defaultStateName
        ?: throw RuntimeException("default stateName not found.")
        val topicState = topic.getState(topicStateName)
            ?: throw RuntimeException("state not found. stateName: $topicStateName")

        return Pair(topic, topicState)
    }

    /**
     *
     */
    internal fun findIntendByTriggerExpression(intends: List<Intend>, requestData: RequestData): Mono<Intend> {
        return intends
            .toFlux()
            .filterWhen { intend ->
                if (intend.triggerExpression == null) {
                    Mono.just(false)
                } else {
                    Mono.defer {
                        if (intend.triggerRequireArguments == true) { //필요할 경우 미리 argument 로딩
                            loadArgument(intend.getIntendArguments(), requestData)
                        } else {
                            Mono.empty()
                        }
                    }.thenReturn(requestData.evaluate(intend.triggerExpression, Boolean::class) ?: false)
                }
            }
            .singleOrEmpty() //TODO 여러개 찾아질 경우에???
    }

    /**
     *
     */
    internal fun findTask(selectedIntend: Intend, requestData: RequestData): Mono<Task> {
        if (selectedIntend.getTasks().isEmpty()) {
            return Mono.error(RuntimeException("task 가 등록되지 않았습니다. selectedIntend: $selectedIntend"))
        }

        if (selectedIntend.getTasks().size == 1) {
            return selectedIntend.getTasks().first().toMono()
        }

        return selectedIntend.getTasks()
            .toFlux()
            .filterWhen { task ->
                Mono.defer {
                    if (task.triggerRequireArguments == true) { //필요할 경우 미리 argument 로딩
                        loadArgument(task.getTaskArguments(), requestData)
                    } else {
                        Mono.empty()
                    }
                }.thenReturn(requestData.evaluate(task.triggerExpression, Boolean::class) ?: false)
            }
            .singleOrEmpty() //TODO 여러개 찾아질 경우에???
    }

    /**
     *
     */
    internal fun executeActions(
        multiTurnRes: MultiTurnRes,
        selectedTask: Task,
        requestData: RequestData,
    ): Mono<List<Action>> {
        val executableActions = selectedTask.getActions().filter { action ->
            val enabled = action.enabledPredicate?.let { enabledPredicate ->
                requestData.evaluate(enabledPredicate, Boolean::class)
            } ?: true

            enabled.also {
                if (enabled.not()) {
                    if (logger.isTraceEnabled) {
                        logger.trace { "실행할 Action 목록에서 제외되었습니다. action: $action" }
                    }
                }
            }
        }

        return executableActions.toFlux()
            .concatMap { action ->
                val actionBehavior = behaviorRegistry.findActionBehaviorOrNull(action.type)
                    ?: return@concatMap Mono.error(ActionBehaviorNotFoundException(action))

                actionBehavior.behave(action, requestData, multiTurnRes)
                    .onErrorMap { ex -> ActionBehaviorRunException(actionBehavior, ex) }
            }
            .then(executableActions.toMono())
    }

    /**
     *
     */
    internal fun loadArgument(
        arguments: List<NameValue<Argument>>,
        requestData: RequestData,
        ): Mono<Void> {
        if (requestData.finishIntendArgumentLoad) { //이미 로딩되었다면 그냥 넘어감
            return Mono.empty()
        }

        return arguments.toFlux()
            .concatMap { (argumentName, argument) ->
                val argumentBehavior = behaviorRegistry.findArgumentBehaviorOrNull(argument.type)
                    ?: return@concatMap Mono.error(ArgumentBehaviorNotFoundException(argument))

                argumentBehavior.behave(argument, requestData)
                    .map { argumentValue -> argumentName to argumentValue }
                    .switchIfEmpty {
                        if (argument.required == true) {
                            Mono.error(ArgumentValueRequiredException(argumentName, argument))
                        } else {
                            Pair(argumentName, null).toMono()
                        }
                    }
            }
            .doOnNext { (argumentName, argumentValue) -> requestData.putArgument(argumentName, argumentValue)}
            .doOnComplete { requestData.finishIntendArgumentLoad = true }
            .then()
    }
}
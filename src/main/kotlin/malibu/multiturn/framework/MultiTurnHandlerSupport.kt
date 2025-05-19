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
        topic: Topic,
        topicState: TopicState,
    ): MultiTurnRes {
        return MultiTurnRes(
            requestId = multiTurnReq.requestId,
            conversationId = multiTurnReq.conversationId,
            intent = multiTurnReq.intent,
            botScenario = botScenario.name,
            topic = topic.name,
            topicState = topicState.name,
            scenarioVersion = botScenario.scenarioVersion,
            modelVersion = botScenario.modelVersion,
        ).also { multiTurnRes ->
            multiTurnRes.setAllConversationParams(multiTurnReq.conversationParams)
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
    internal fun findIntendByTriggerExpression(intends: List<Intend>, intendData: IntendData): Mono<Intend> {
        return intends
            .toFlux()
            .filterWhen { intend ->
                if (intend.triggerExpression == null) {
                    Mono.just(false)
                } else {
                    Mono.defer {
                        if (intend.triggerRequireArguments == true) { //필요할 경우 미리 argument 로딩
                            loadArgument(intend.getIntendArguments(), intendData)
                        } else {
                            Mono.empty()
                        }
                    }.thenReturn(intendData.evaluate(intend.triggerExpression, Boolean::class) ?: false)
                }
            }
            .singleOrEmpty() //TODO 여러개 찾아질 경우에???
    }

    /**
     *
     */
    internal fun findTask(selectedIntend: Intend, intendData: IntendData): Mono<Task> {
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
                        loadArgument(task.getTaskArguments(), intendData)
                    } else {
                        Mono.empty()
                    }
                }.thenReturn(intendData.evaluate(task.triggerExpression, Boolean::class) ?: false)
            }
            .singleOrEmpty() //TODO 여러개 찾아질 경우에???
    }

    /**
     *
     */
    internal fun executeActions(
        multiTurnRes: MultiTurnRes,
        selectedTask: Task,
        intendData: IntendData,
    ): Mono<List<Action>> {
        val executableActions = selectedTask.getActions().filter { action ->
            val enabled = action.enabledPredicate?.let { enabledPredicate ->
                intendData.evaluate(enabledPredicate, Boolean::class)
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

                actionBehavior.behave(action, intendData, multiTurnRes)
                    .onErrorMap { ex -> ActionBehaviorRunException(actionBehavior, ex) }
            }
            .then(executableActions.toMono())
    }

    /**
     *
     */
    internal fun loadArgument(
        arguments: List<NameValue<Argument>>,
        intendData: IntendData,
        ): Mono<Void> {
        if (intendData.finishIntendArgumentLoad) { //이미 로딩되었다면 그냥 넘어감
            return Mono.empty()
        }

        return arguments.toFlux()
            .concatMap { (argumentName, argument) ->
                val argumentBehavior = behaviorRegistry.findArgumentBehaviorOrNull(argument.type)
                    ?: return@concatMap Mono.error(ArgumentBehaviorNotFoundException(argument))

                argumentBehavior.behave(argument, intendData)
                    .map { argumentValue -> argumentName to argumentValue }
                    .switchIfEmpty {
                        if (argument.required == true) {
                            Mono.error(ArgumentValueRequiredException(argumentName, argument))
                        } else {
                            Pair(argumentName, null).toMono()
                        }
                    }
            }
            .doOnNext { (argumentName, argumentValue) -> intendData.putArgument(argumentName, argumentValue)}
            .doOnComplete { intendData.finishIntendArgumentLoad = true }
            .then()
    }
}
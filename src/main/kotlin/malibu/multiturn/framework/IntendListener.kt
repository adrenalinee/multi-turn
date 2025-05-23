package malibu.multiturn.framework

import malibu.multiturn.model.Task
import reactor.core.publisher.Mono

interface IntendListener {

    /**
     * taskSelector 가 실행되기 전에 호출되며, 응답으로 해당 listener 가
     * TasksRun event( beforeTasksRun(), afterTasksRun() ) 을 호출할지 응답한다.
     * @return - false 일 경우에 해당 리스너가 할일이 없다는 의미. task 관련 다른 이벤트들(beforeTasksRun, afterTasksRun)은 발생하지 않는다.
     */
    fun beforeTaskSelect(intendData: IntendData): Mono<Boolean> = Mono.just(true)

    /**
     * task run 실행 직전
     */
    fun beforeTasksRun(intendData: IntendData): Mono<Unit> = Mono.empty()

    /**
     * task run 실행 직후
     */
    fun afterTasksRun(intendData: IntendData, selectedTask: Task, multiTurnRes: MultiTurnRes): Mono<Unit> = Mono.empty()

    /**
     * task 후 IntendData 처리까지 끝난 후에 실행
     */
    fun afterTasksRunCompletion(intendData: IntendData, selectedTask: Task, multiTurnRes: MultiTurnRes): Mono<Unit> = Mono.empty()

    /**
     * task 실행중에 에러가 발생했을때 실생됨
     */
    fun onTaskError(intendData: IntendData, ex: Throwable): Mono<Unit> = Mono.empty()
}
package malibu.multiturn.framework.exception

import malibu.multiturn.framework.TaskListener

class AfterTasksRunException(
    taskListener: TaskListener,
    cause: Throwable
): MultiTurnException(
    message = "intendListener.afterTasksRun() 실행중 에러 발생. intendListener: $taskListener",
    cause = cause
)
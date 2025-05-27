package malibu.multiturn.framework.exception

import malibu.multiturn.framework.TaskListener

class BeforeTasksRunException(
    taskListener: TaskListener,
    cause: Throwable
): MultiTurnException(
    message = "intendListener.beforeTasksRun() 실행중 에러 발생. intendListener: $taskListener",
    cause = cause
)
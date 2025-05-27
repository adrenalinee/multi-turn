package malibu.multiturn.framework.exception

import malibu.multiturn.framework.TaskListener

class BeforeTaskSelectRunException(
    taskListener: TaskListener,
    cause: Throwable
): MultiTurnException(
    message = "intendListener.beforeTaskSelect() 실행중 에러 발생. intendListener: $taskListener",
    cause = cause
)
package malibu.multiturn.framework.exception

import malibu.multiturn.framework.IntendListener

class BeforeTasksRunException(
    intendListener: IntendListener,
    cause: Throwable
): MultiTurnException(
    message = "intendListener.beforeTasksRun() 실행중 에러 발생. intendListener: $intendListener",
    cause = cause
)
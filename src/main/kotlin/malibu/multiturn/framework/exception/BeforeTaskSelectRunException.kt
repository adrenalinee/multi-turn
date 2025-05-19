package malibu.multiturn.framework.exception

import malibu.multiturn.framework.IntendListener

class BeforeTaskSelectRunException(
    intendListener: IntendListener,
    cause: Throwable
): MultiTurnException(
    message = "intendListener.beforeTaskSelect() 실행중 에러 발생. intendListener: $intendListener",
    cause = cause
)
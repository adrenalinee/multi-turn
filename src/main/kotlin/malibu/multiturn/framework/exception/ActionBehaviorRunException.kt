package malibu.multiturn.framework.exception

import malibu.multiturn.framework.ActionBehavior

class ActionBehaviorRunException(
    actionBehavior: ActionBehavior<*>,
    cause: Throwable
): MultiTurnException(
    message = "actionBehavior 실행중에 에러가 발생했습니다. actionBehavior: $actionBehavior",
    cause = cause
)
package malibu.multiturn.framework.exception

import malibu.multiturn.model.Argument

class ArgumentBehaviorNotFoundException(
    argument: Argument
): MultiTurnException(
    message = "argumentBehavior 를 찾을 수 없습니다. argument: $argument"
)

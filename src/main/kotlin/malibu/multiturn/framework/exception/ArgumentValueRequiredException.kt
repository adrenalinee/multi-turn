package malibu.multiturn.framework.exception

import malibu.multiturn.model.Argument

class ArgumentValueRequiredException(
    argumentName: String,
    val argument: Argument
): MultiTurnException(
    message = "argument value 가 null 입니다. 필수(required = true)로 지정된 argument 는 null 을 허용하지 않습니다." +
            " argument name: $argumentName, argument: $argument"
) {
}
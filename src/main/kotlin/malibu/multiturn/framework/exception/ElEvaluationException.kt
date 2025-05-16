package malibu.multiturn.framework.exception

open class ElEvaluationException(
    message: String,
    cause: Throwable? = null
): MultiTurnException(message, cause)
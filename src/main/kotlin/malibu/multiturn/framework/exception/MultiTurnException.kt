package malibu.multiturn.framework.exception

/**
 * multi turn framework 최상위 에러.
 */
open class MultiTurnException(
    message: String,
    cause: Throwable? = null
): RuntimeException(message, cause)

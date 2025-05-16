package malibu.multiturn.framework.exception

import org.springframework.expression.spel.SpelEvaluationException

class SpelEvaluationMultiTurnException(
    el: String,
    override val cause: Exception,
): ElEvaluationException(
    message = "spel 표현식을 처리하는중에 에러가 발생했습니다. " +
            "el: \"$el\", " +
            "message: ${getExtraMessage(cause)}",
)

private fun getExtraMessage(cause: Exception): String {
    return when(cause) {
        is SpelEvaluationException -> "messageCode: ${cause.messageCode}, position: ${cause.position},  message: ${cause.message}"
        else -> cause.toString()
    }
}
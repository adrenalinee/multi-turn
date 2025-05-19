package malibu.multiturn.module.core

import malibu.multiturn.model.Argument

/**
 * 표현식이 리턴한 값을 argument 로 사용.
 */
class ExpressionArgument(
    /**
     * argument value 를 리턴하는 표현식.
     * Mono, Flux 사용가능.
     * Mono 의 경우 mono 에서 꺼낸 값을 전달.
     * Flux 의 경우 flux 에서 꺼낸 값들을 List 로 묶어서 전달.
     */
    val expression: String
) : Argument(
    type = TYPE,
) {
    companion object {
        val TYPE: String = ExpressionArgument::class.java.simpleName
    }
}

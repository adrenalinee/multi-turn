package malibu.multiturn.framework.expression

import kotlin.reflect.KClass

/**
 * 표현식 파서 추상화
 */
interface ExpressionParser {

    /**
     * @param expression 표현식. 현재 spel 기반
     * @param expressionRoot 표현식에서 참조할 root 객체
     * @param desiredResultType 표현식이 응답할 것으로 예상하는 타입.
     * @param temporaryVariables 지속적이지 않고 이번 한번의 표현식 파싱에서만 사용할 임시 변수
     * @param T 표현식이 응답하는 객체 타입
     */
    fun <T: Any> evaluate(
        expression: String,
        expressionRoot: ExpressionRoot,
        desiredResultType: KClass<T>,
        temporaryVariables: Map<String, Any> = emptyMap()
    ): T?

    /**
     * 표현식에서 봇 템플릿 단위로 계속 사용할 수 있는 변수를 등록.
     * 변수의 값으로 함수도 사용가능
     */
    fun registerVariable(name: String, value: Any)

    fun getVariables(): Map<String, Any>
}

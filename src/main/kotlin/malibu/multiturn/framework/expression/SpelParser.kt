package malibu.multiturn.framework.expression

import malibu.multiturn.framework.ExpressionParser
import malibu.multiturn.framework.ExpressionRoot
import malibu.multiturn.framework.exception.SpelEvaluationMultiTurnException
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.DataBindingMethodResolver
import org.springframework.expression.spel.support.SimpleEvaluationContext
import kotlin.reflect.KClass

/**
 * spring 의 spel 기반의 표현식 파서
 * 파서 스팩: https://docs.spring.io/spring-framework/reference/core/expressions.html
 */
class SpelParser: ExpressionParser {

    /**
     * 표현식(expression language) 를 통해 intentData 에 접근하게 할 수 있는 파서
     */
    private val parser = SpelExpressionParser()

    private val moduleVariables = mutableMapOf<String, Any>()

    /**
     *
     */
    override fun <T : Any> evaluate(
        expression: String,
        expressionRoot: ExpressionRoot,
        desiredResultType: KClass<T>,
        temporaryVariables: Map<String, Any>
    ): T? {
        //TODO expressionContext 를 매번 만들지 않고 intent 단위로 하나만 만들어도 되게 처리되면 메모리 관리 차원에서 좋을것임.
        val methodResolver = DataBindingMethodResolver.forInstanceMethodInvocation()

        val evalContext = SimpleEvaluationContext.forReadOnlyDataBinding()
            .withMethodResolvers(methodResolver)
            .withRootObject(expressionRoot)
            .build()

        //TODO 변수 이름이 중복되면 기존에 다른 변수를 덮어쓸 수 있어서 해결법이 필요함.
        temporaryVariables.forEach { (varName, varValue) ->
            evalContext.setVariable(varName, varValue)
        }

        moduleVariables.forEach { (name, value) ->
            evalContext.setVariable(name, value)
            methodResolver.registerMethodFilter(value::class.java) { it }
        }

        try {
            return parser.parseExpression(expression)
                .getValue(evalContext, desiredResultType.java)
        } catch (ex: Exception) {
            throw SpelEvaluationMultiTurnException(expression, ex)
        }
    }

    /**
     *
     */
    override fun registerVariable(name: String, value: Any) {
        moduleVariables.put(name, value)
    }

    /**
     *
     */
    override fun getVariables(): Map<String, Any> {
        return moduleVariables.toMap()
    }
}

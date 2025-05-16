package malibu.multiturn.framework.config

import malibu.multiturn.framework.expression.ExpressionParser

internal interface ExpressionVariableFunc {
    fun register(expressionParser: ExpressionParser)
}
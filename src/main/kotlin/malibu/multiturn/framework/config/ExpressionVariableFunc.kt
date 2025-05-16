package malibu.multiturn.framework.config

import malibu.multiturn.framework.ExpressionParser

internal interface ExpressionVariableFunc {
    fun register(expressionParser: ExpressionParser)
}
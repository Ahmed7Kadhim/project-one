package com.ahmed7kadhim.projectone

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

private const val ERROR = "Error"
private const val DIVISION_PRECISION = 16

enum class Operator(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×"),
    DIVIDE("÷"),
    MODULO("%")
}

class CalculatorEngine {
    private var input = "0"
    private var accumulator: BigDecimal? = null
    private var pendingOperator: Operator? = null
    private var expression = ""
    private var error = false

    fun clear() {
        input = "0"
        accumulator = null
        pendingOperator = null
        expression = ""
        error = false
    }

    fun restore(expressionText: String, resultText: String) {
        expression = expressionText
        input = resultText.ifBlank { "0" }
        accumulator = null
        pendingOperator = null
        error = resultText == ERROR
    }

    fun inputDigit(digit: Char) {
        if (error) clear()
        if (digit !in '0'..'9') return

        input = if (input == "0") {
            digit.toString()
        } else {
            input + digit
        }
    }

    fun inputDecimalPoint() {
        if (error) clear()
        if (!input.contains('.')) {
            input += "."
        }
    }

    fun setOperator(operator: Operator) {
        if (error) return

        val current = toNumber(input)
        if (current == null) {
            showError()
            return
        }

        if (accumulator == null) {
            accumulator = current
        } else if (pendingOperator != null) {
            val computed = compute(accumulator!!, current, pendingOperator!!)
            if (computed == null) {
                showError()
                return
            }
            accumulator = computed
            input = format(computed)
        }

        pendingOperator = operator
        expression = "${format(accumulator!!)} ${operator.symbol}"
        input = "0"
    }

    fun evaluate() {
        if (error) return

        val lhs = accumulator
        val op = pendingOperator
        val rhs = toNumber(input)

        if (lhs == null || op == null || rhs == null) {
            return
        }

        val computed = compute(lhs, rhs, op)
        if (computed == null) {
            showError()
            return
        }

        expression = "${format(lhs)} ${op.symbol} ${format(rhs)} ="
        input = format(computed)
        accumulator = null
        pendingOperator = null
    }

    fun expressionText(): String = expression

    fun resultText(): String = input

    private fun compute(left: BigDecimal, right: BigDecimal, operator: Operator): BigDecimal? {
        return when (operator) {
            Operator.ADD -> left + right
            Operator.SUBTRACT -> left - right
            Operator.MULTIPLY -> left * right
            Operator.DIVIDE -> {
                if (right.compareTo(BigDecimal.ZERO) == 0) return null
                left.divide(right, MathContext(DIVISION_PRECISION, RoundingMode.HALF_UP))
            }
            Operator.MODULO -> {
                if (right.compareTo(BigDecimal.ZERO) == 0) return null
                left.remainder(right)
            }
        }
    }

    private fun toNumber(value: String): BigDecimal? {
        return value.toBigDecimalOrNull()
    }

    private fun showError() {
        input = ERROR
        expression = ""
        accumulator = null
        pendingOperator = null
        error = true
    }

    private fun format(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        return stripped.toPlainString()
    }
}

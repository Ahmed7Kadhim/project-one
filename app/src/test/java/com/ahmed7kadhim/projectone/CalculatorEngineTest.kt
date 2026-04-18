package com.ahmed7kadhim.projectone

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun chainedOperations_workCorrectly() {
        val engine = CalculatorEngine()
        engine.inputDigit('2')
        engine.setOperator(Operator.ADD)
        engine.inputDigit('3')
        engine.setOperator(Operator.MULTIPLY)
        engine.inputDigit('4')
        engine.evaluate()

        assertEquals("20", engine.resultText())
    }

    @Test
    fun divide_returnsDecimalResult() {
        val engine = CalculatorEngine()
        engine.inputDigit('7')
        engine.setOperator(Operator.DIVIDE)
        engine.inputDigit('2')
        engine.evaluate()

        assertEquals("3.5", engine.resultText())
    }

    @Test
    fun modulo_worksCorrectly() {
        val engine = CalculatorEngine()
        engine.inputDigit('7')
        engine.setOperator(Operator.MODULO)
        engine.inputDigit('3')
        engine.evaluate()

        assertEquals("1", engine.resultText())
    }

    @Test
    fun divideByZero_showsError() {
        val engine = CalculatorEngine()
        engine.inputDigit('9')
        engine.setOperator(Operator.DIVIDE)
        engine.inputDigit('0')
        engine.evaluate()

        assertEquals("Error", engine.resultText())
    }
}

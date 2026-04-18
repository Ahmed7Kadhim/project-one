package com.ahmed7kadhim.projectone

import android.content.Context
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

class MainActivity : AppCompatActivity() {
    private lateinit var expressionView: TextView
    private lateinit var resultView: TextView
    private lateinit var scrollContainer: NestedScrollView
    private val engine = CalculatorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expressionView = findViewById(R.id.tvExpression)
        resultView = findViewById(R.id.tvResult)
        scrollContainer = findViewById(R.id.scrollContainer)

        restoreLastResult()
        bindButtons()
        render()
    }

    override fun onStop() {
        super.onStop()
        saveLastResult()
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (
            event.action == MotionEvent.ACTION_SCROLL &&
            event.isFromSource(InputDevice.SOURCE_ROTARY_ENCODER)
        ) {
            val delta = -event.getAxisValue(MotionEvent.AXIS_SCROLL)
            scrollContainer.scrollBy(0, (delta * 60).toInt())
            return true
        }
        return super.onGenericMotionEvent(event)
    }

    private fun bindButtons() {
        val digitMap = mapOf(
            R.id.btn0 to '0',
            R.id.btn1 to '1',
            R.id.btn2 to '2',
            R.id.btn3 to '3',
            R.id.btn4 to '4',
            R.id.btn5 to '5',
            R.id.btn6 to '6',
            R.id.btn7 to '7',
            R.id.btn8 to '8',
            R.id.btn9 to '9'
        )

        digitMap.forEach { (id, digit) ->
            findViewById<Button>(id).setOnClickListener {
                haptic(it)
                engine.inputDigit(digit)
                render()
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener {
            haptic(it)
            engine.inputDecimalPoint()
            render()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            haptic(it)
            engine.clear()
            render()
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperatorClick(it, Operator.ADD) }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { onOperatorClick(it, Operator.SUBTRACT) }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorClick(it, Operator.MULTIPLY) }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorClick(it, Operator.DIVIDE) }
        findViewById<Button>(R.id.btnModulo).setOnClickListener { onOperatorClick(it, Operator.MODULO) }

        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            haptic(it)
            engine.evaluate()
            render()
        }
    }

    private fun onOperatorClick(view: View, operator: Operator) {
        haptic(view)
        engine.setOperator(operator)
        render()
    }

    private fun render() {
        expressionView.text = engine.expressionText()
        resultView.text = engine.resultText()
    }

    private fun haptic(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    private fun saveLastResult() {
        getSharedPreferences("calculator", Context.MODE_PRIVATE)
            .edit()
            .putString("expression", engine.expressionText())
            .putString("result", engine.resultText())
            .apply()
    }

    private fun restoreLastResult() {
        val prefs = getSharedPreferences("calculator", Context.MODE_PRIVATE)
        val expression = prefs.getString("expression", "") ?: ""
        val result = prefs.getString("result", "0") ?: "0"
        engine.restore(expression, result)
    }
}

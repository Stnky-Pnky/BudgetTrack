
package com.budgettrack.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screen = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 70, 40, 30)
            setBackgroundColor(Color.rgb(16, 29, 48))
        }

        val heading = TextView(this).apply {
            text = "BudgetTrack"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Take control of your money"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 15, 0, 60)
        }

        val balance = TextView(this).apply {
            text = "Welcome to your budget!"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 30)
        }

        screen.addView(heading)
        screen.addView(subtitle)
        screen.addView(balance)

        val features = listOf(
            "My Bills",
            "My Income",
            "Everyday Expenses",
            "Budget Reports",
            "Cash Flow Forecast"
        )

        features.forEach { feature ->
            val button = Button(this).apply {
                text = feature
                setOnClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        "$feature - Coming soon!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            screen.addView(button)
        }

        setContentView(screen)
    }
}


package com.budgettrack.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class MainActivity : Activity() {
    private val bills = mutableListOf<Bill>()
    private lateinit var screen: LinearLayout

    data class Bill(
        var name: String,
        var amount: Double,
        var dueDay: Int,
        var frequency: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadBills()
        showHome()
    }

    private fun baseScreen(title: String) {
        val scroll = ScrollView(this)
        screen = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 45, 30, 40)
            setBackgroundColor(Color.rgb(16, 29, 48))
        }
        scroll.addView(screen)
        setContentView(scroll)
        addText(title, 27f)
    }

    private fun addText(value: String, size: Float = 18f) {
        screen.addView(TextView(this).apply {
            text = value
            textSize = size
            setTextColor(Color.WHITE)
            setPadding(4, 14, 4, 14)
        })
    }

    private fun addButton(label: String, action: () -> Unit) {
        screen.addView(Button(this).apply {
            text = label
            setOnClickListener { action() }
        })
    }

    private fun showHome() {
        baseScreen("BudgetTrack")
        addText("Take control of your money", 16f)
        addButton("My Bills") { showBills() }

        listOf(
            "My Income",
            "Everyday Expenses",
            "Budget Reports",
            "Cash Flow Forecast"
        ).forEach { feature ->
            addButton(feature) {
                Toast.makeText(
                    this, "$feature - Coming soon!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showBills() {
        baseScreen("My Bills")
        addButton("+ Add Bill") { editBill(null) }

        val monthlyTotal = bills.sumOf {
            when (it.frequency) {
                "Weekly" -> it.amount * 52.0 / 12.0
                "Every 2 weeks" -> it.amount * 26.0 / 12.0
                "Yearly" -> it.amount / 12.0
                else -> it.amount
            }
        }

        addText(
            "Estimated monthly bills: ${money(monthlyTotal)}",
            19f
        )

        if (bills.isEmpty()) {
            addText("No bills added yet.")
        }

        bills.forEachIndexed { index, bill ->
            addText(
                "${bill.name}\n" +
                "${money(bill.amount)} • ${bill.frequency}\n" +
                "Due day: ${bill.dueDay}"
            )
            addButton("Edit ${bill.name}") {
                editBill(index)
            }
            addButton("Delete ${bill.name}") {
                AlertDialog.Builder(this)
                    .setTitle("Delete ${bill.name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        bills.removeAt(index)
                        saveBills()
                        showBills()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        addButton("← Back to Home") { showHome() }
    }

    private fun editBill(index: Int?) {
        val existing = index?.let { bills[it] }

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35, 15, 35, 5)
        }

        val nameInput = EditText(this).apply {
            hint = "Bill name (e.g. Rent)"
            setSingleLine(true)
            setText(existing?.name ?: "")
        }

        val amountInput = EditText(this).apply {
            hint = "Amount (e.g. 850.00)"
            inputType = 8194
            setText(existing?.amount?.toString() ?: "")
        }

        val dayInput = EditText(this).apply {
            hint = "Due day (1-31)"
            inputType = 2
            setText(existing?.dueDay?.toString() ?: "")
        }

        val frequencies = arrayOf(
            "Monthly", "Weekly", "Every 2 weeks", "Yearly"
        )

        val frequencyInput = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                frequencies
            )
            setSelection(
                frequencies.indexOf(existing?.frequency ?: "Monthly")
                    .coerceAtLeast(0)
            )
        }

        form.addView(nameInput)
        form.addView(amountInput)
        form.addView(dayInput)
        form.addView(frequencyInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (index == null) "Add Bill" else "Edit Bill")
            .setView(form)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val name = nameInput.text.toString().trim()
                    val amount = amountInput.text.toString().toDoubleOrNull()
                    val day = dayInput.text.toString().toIntOrNull()

                    if (name.isEmpty() ||
                        amount == null ||
                        !amount.isFinite() ||
                        amount < 0 ||
                        day == null ||
                        day !in 1..31
                    ) {
                        Toast.makeText(
                            this,
                            "Enter a name, valid amount, and day 1-31.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val bill = Bill(
                            name,
                            amount,
                            day,
                            frequencyInput.selectedItem.toString()
                        )

                        if (index == null) {
                            bills.add(bill)
                        } else {
                            bills[index] = bill
                        }

                        saveBills()
                        dialog.dismiss()
                        showBills()
                    }
                }
        }
        dialog.show()
    }

    private fun money(value: Double): String =
        NumberFormat.getCurrencyInstance(Locale.US).format(value)

    private fun saveBills() {
        val array = JSONArray()
        bills.forEach { bill ->
            array.put(JSONObject().apply {
                put("name", bill.name)
                put("amount", bill.amount)
                put("dueDay", bill.dueDay)
                put("frequency", bill.frequency)
            })
        }
        getPreferences(MODE_PRIVATE)
            .edit()
            .putString("bills", array.toString())
            .apply()
    }

    private fun loadBills() {
        bills.clear()
        try {
            val saved = getPreferences(MODE_PRIVATE)
                .getString("bills", "[]") ?: "[]"
            val array = JSONArray(saved)

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                bills.add(
                    Bill(
                        item.getString("name"),
                        item.getDouble("amount"),
                        item.getInt("dueDay"),
                        item.getString("frequency")
                    )
                )
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Could not load saved bills.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

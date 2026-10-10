package com.example.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    private val currencyFormat: NumberFormat = NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    fun formatCurrency(amount: Double): String {
        return "₹" + currencyFormat.format(amount)
    }

    fun formatNumber(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun parseAmount(text: String): Double {
        return text.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
    }
}

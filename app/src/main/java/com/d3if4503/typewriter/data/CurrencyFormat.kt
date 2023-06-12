package com.d3if4503.typewriter.data

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun formatCurrencyId(value: Double): String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.minimumFractionDigits = 0
    return numberFormat.format(value)
}

fun formatCurrencyId(value: BigDecimal): String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.minimumFractionDigits = 0
    return numberFormat.format(value)
}
package com.hivestudio.ui.format

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object RubleFormatter {
    private val symbols = DecimalFormatSymbols(
        Locale.Builder()
            .setLanguage("ru")
            .setRegion("RU")
            .build()
    ).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }

    private val amountFormat = DecimalFormat("#,###", symbols)

    fun format(amountRubles: Int): String = "${amountFormat.format(amountRubles)} ₽"
}

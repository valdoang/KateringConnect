package com.valdoang.kateringconnect.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import java.math.RoundingMode
import java.text.*
import java.util.*

fun String.withNumberingFormat(): String {
    return NumberFormat.getNumberInstance().format(this.toDouble())
}

fun Long.withNumberingFormat(): String {
    return NumberFormat.getNumberInstance().format(this.toDouble())
}

fun Double.withNumberingFormat(): String {
    return NumberFormat.getNumberInstance().format(this)
}

fun String.withCurrencyFormat(): String {
    val mCurrencyFormat = NumberFormat.getCurrencyInstance()
    return mCurrencyFormat.format(this.toLong())
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}

fun String.withTimestamptoDateFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = format.format(this.toLong())
    val parse = format.parse(date) as Date
    return DateFormat.getDateInstance(DateFormat.LONG).format(parse)
}

fun String.withTimestamptoTimeFormat(): String {
    val format = SimpleDateFormat("H:mm", Locale.getDefault())
    val date = format.format(this.toLong())
    val parse = format.parse(date) as Date
    return DateFormat.getTimeInstance(DateFormat.SHORT).format(parse)
}

fun String.withTimestampToDateTimeFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy H:mm", Locale.getDefault())
    val date = format.format(this.toLong())
    val parse = format.parse(date) as Date
    val dateParse = DateFormat.getDateInstance(DateFormat.LONG).format(parse)
    val timeParse = DateFormat.getTimeInstance(DateFormat.SHORT).format(parse)
    return "$dateParse $timeParse"
}

fun String.withTimestampToDateTimeFormat2(): String {
    val format = SimpleDateFormat("dd/MM/yyyy H:mm", Locale.getDefault())
    val date = format.format(this.toLong())
    val parse = format.parse(date) as Date
    val dateParse = DateFormat.getDateInstance(DateFormat.SHORT).format(parse)
    val timeParse = DateFormat.getTimeInstance(DateFormat.SHORT).format(parse)
    return "$dateParse $timeParse"
}

fun EditText.allChangedListener(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            onTextChanged.invoke(s.toString())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun EditText.beforeChangedListener(button: Button) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            button.isEnabled = false
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            button.isEnabled = !s.isNullOrBlank()
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun Double.roundOffDecimal(): String {
    val df = DecimalFormat("#.#", DecimalFormatSymbols(Locale.ENGLISH))
    df.roundingMode = RoundingMode.CEILING
    return df.format(this)
}
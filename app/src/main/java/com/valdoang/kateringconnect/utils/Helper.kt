package com.valdoang.kateringconnect.utils

import java.text.NumberFormat

fun String.withNumberingFormat(): String {
    return NumberFormat.getNumberInstance().format(this.toDouble())
}

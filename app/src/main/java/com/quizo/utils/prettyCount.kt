package com.quizo.utils

import java.text.DecimalFormat

class prettyCount {
    companion object{
        fun pretty(number: Number): String {
            val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
            val numValue = number.toLong()
            val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
            val base = value / 3
            return if (value >= 3 && base < suffix.size) {
                DecimalFormat("#0.00").format(numValue / Math.pow(10.0, (base * 3).toDouble())) + suffix[base]
            } else {
                DecimalFormat().format(numValue)
            }
        }
    }

}
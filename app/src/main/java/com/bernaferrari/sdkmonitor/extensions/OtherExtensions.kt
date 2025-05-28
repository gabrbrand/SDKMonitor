package com.bernaferrari.sdkmonitor.extensions

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Modern extension functions for the SDK Monitor app
 */

internal fun Long.convertTimestampToDate(): String {
    return if (this == 0L) {
        "Never"
    } else {
        val now = System.currentTimeMillis()
        val diff = now - this
        
        when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000} minutes ago"
            diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
            diff < 604_800_000 -> "${diff / 86_400_000} days ago"
            else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(this))
        }
    }
}

internal operator fun Boolean.inc() = !this

inline val @receiver:ColorInt Int.darken
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.BLACK, 0.2f)

inline val @receiver:ColorInt Int.lighten
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.WHITE, 0.2f)

// colors inspired from https://www.vanschneider.com/colors
fun Int.apiToColor(): Int = when (this) {
    in 0..31 -> 0xFFD31B33.toInt() // red
    32 -> 0xFFE54B4B.toInt() // red-orange
    33 -> 0xFFE37A46.toInt() // orange
    34 -> 0XFF178E96.toInt() // blue-green
    else -> 0xFF14B572.toInt() // green
}

fun Int.apiToVersion() = when (this) {
    3 -> "Cupcake"
    4 -> "Donut"
    5, 6, 7 -> "Eclair"
    8 -> "Froyo"
    9, 10 -> "Gingerbread"
    11, 12, 13 -> "Honeycomb"
    14, 15 -> "Ice Cream Sandwich"
    16, 17, 18 -> "Jelly Bean"
    19, 20 -> "KitKat"
    21, 22 -> "Lollipop"
    23 -> "Marshmallow"
    24, 25 -> "Nougat"
    26, 27 -> "Oreo"
    28 -> "Pie"
    29, 30, 31 -> "Android ${this - 19}"
    32 -> "Android 12L"
    else -> "Android ${this - 20}"
}
package com.example.sling.extension

import java.time.LocalDate

fun Int.toDateString(): String =
    String.format("%02d:%02d:%02d", this.div(3600), this.div(60), this.mod(60))

fun LocalDate.parseToString(): String = "${this.year}.${this.monthValue}.${this.dayOfMonth}"

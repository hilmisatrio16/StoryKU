package com.hilmisatrio.storyku.utils

import android.annotation.SuppressLint
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.creationDate(): String {
    return dateDifference(this)
}

@SuppressLint("SimpleDateFormat")
fun dateDifference(date: String): String {
    val creationDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    creationDateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val dateUTC = creationDateFormat.parse(date)

    val currentDate = Date()
    val timeDifference = DateUtils.getRelativeTimeSpanString(
        dateUTC.time,
        currentDate.time,
        DateUtils.MINUTE_IN_MILLIS
    )
    return timeDifference.toString()
}




package com.netology.nework.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object AndroidUtils {

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(date: String): String{
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        return formattedDate
    }

}
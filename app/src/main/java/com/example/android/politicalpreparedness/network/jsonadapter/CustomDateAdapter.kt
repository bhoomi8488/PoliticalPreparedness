package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class CustomDateAdapter {
    @ToJson
    fun toJson(value: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(dateFormat)
    }

    @FromJson
    fun fromJson(value: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(value)
    }
}
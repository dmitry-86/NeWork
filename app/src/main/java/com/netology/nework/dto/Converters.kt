package com.netology.nework.dto

import androidx.room.TypeConverter
import com.netology.nework.enumeration.AttachmentType

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun fromSet(set: Set<Long>): String = set.joinToString(",")

    @TypeConverter
    fun toSet(data: String): Set<Long> = if (data.isBlank()) emptySet() else data.split(",").map { it.toLong() }.toSet()
}
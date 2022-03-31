package com.netology.nework.dto

import com.netology.nework.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
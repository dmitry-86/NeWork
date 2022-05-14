package com.netology.nework.dto

import java.io.File
import java.io.InputStream

data class Media(val url: String)

data class MediaUpload(val inputStream: InputStream)

data class PhotoUpload(val file: File)
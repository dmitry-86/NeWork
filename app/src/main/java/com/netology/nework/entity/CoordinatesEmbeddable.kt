package com.netology.nework.entity

import com.netology.nework.dto.Coordinates

data class CoordinatesEmbeddable(
    var lat: Double,
    var long: Double,
) {
    fun toDto() = Coordinates(lat, long)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordinatesEmbeddable(it.lat, it.long)
        }
    }
}
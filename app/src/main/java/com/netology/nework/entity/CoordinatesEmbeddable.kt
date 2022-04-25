package com.netology.nework.entity

import com.netology.nework.dto.Coordinates

data class CoordinatesEmbeddable(
    var lat: Double? = null,
    var long: Double? = null,
) {

    fun toCoordinates(): Coordinates = Coordinates(lat = lat ?: 0.0, long = long ?: 0.0)

    companion object {
        fun fromCoordinates(coordinates: Coordinates): CoordinatesEmbeddable =
            with(coordinates) {
                CoordinatesEmbeddable(lat = lat, long = long)
            }
    }
}
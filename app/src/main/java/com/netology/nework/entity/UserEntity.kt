package com.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nework.dto.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
) {
    fun toDto() =
        User(
            id = id,
            login = login,
            name = name,
            avatar = avatar
        )

    companion object {
        fun fromDto(dto: User) =
            UserEntity(
                dto.id,
                dto.login,
                dto.name,
                dto.avatar
            )
    }
}

fun List<UserEntity>.toDto() = map(UserEntity::toDto)
fun List<User>.toEntity() = map(UserEntity::fromDto)
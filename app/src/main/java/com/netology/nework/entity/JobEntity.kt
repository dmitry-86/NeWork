package com.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nework.dto.Job

//@Entity
//data class JobEntity(
//    @PrimaryKey(autoGenerate = true)
//    var id: Long,
//    var user: UserEntity,
//    /**
//     * Название компании
//     */
//    var name: String,
//    var position: String,
//    /**
//     * Дата и время начала работы
//     */
//    var start: Long,
//    /**
//     * Дата и время окончания работы
//     */
//    var finish: Long? = null,
//    /**
//     * Ссылка на веб-сайт организации
//     */
//    var link: String? = null,
//) {
//    fun toDto(myId: Long) = Job(id, name, position, start, finish, link)
//
//    companion object {
//        fun fromDto(dto: Job, myId: Long) = JobEntity(
//            dto.id,
//            UserEntity(myId),
//            dto.name,
//            dto.position,
//            dto.start,
//            dto.finish,
//            dto.link,
//        )
//    }
//}
package com.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var authorId: Long,
    /**
     * Название компании
     */
    var name: String,
    var position: String,
    /**
     * Дата и время начала работы
     */
    var start: Long,
    /**
     * Дата и время окончания работы
     */
    var finish: Long? = null,
    /**
     * Ссылка на веб-сайт организации
     */
    var link: String? = null,
) {
    fun toDto() = Job(id, authorId, name, position, start, finish, link)

    companion object {
        fun fromDto(dto: Job) = JobEntity(
            dto.id,
            dto.authorId,
            dto.name,
            dto.position,
            dto.start,
            dto.finish,
            dto.link,
        )
    }
}

fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)
fun List<Job>.toEntity(): List<JobEntity> = map(JobEntity::fromDto)
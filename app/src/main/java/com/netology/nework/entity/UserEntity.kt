package com.netology.nework.entity

import androidx.room.Entity
import com.netology.nework.dto.User

//@Entity
//data class UserEntity(
//    var id: Long,
//    var authorId: Long,
//    var login: String,
//    var pass: String,
//    var name: String,
//    var avatar: String? = null,
//)    : UserDetails {
//    constructor(id: Long) : this(id, "", "", "", "")}
//
//    override fun getUsername(): String = login
//    override fun getPassword(): String = pass
//    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER"))
//    override fun isAccountNonExpired(): Boolean = true
//    override fun isAccountNonLocked(): Boolean = true
//    override fun isCredentialsNonExpired(): Boolean = true
//    override fun isEnabled(): Boolean = true
//
//    fun toDto() = User(id, login, name, avatar, authorities.map(GrantedAuthority::getAuthority))
//}
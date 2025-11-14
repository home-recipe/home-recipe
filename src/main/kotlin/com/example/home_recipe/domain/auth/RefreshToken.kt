package com.example.home_recipe.domain.auth

import com.example.home_recipe.domain.user.User
import jakarta.persistence.*

@Table(name = "refresh_token")
@Entity
class RefreshToken(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val user: User,

    @Column(nullable = false)
    var refreshToken: String
) {
    constructor(
        user: User,
        refreshToken: String
    ) : this(
        id = null,
        user = user,
        refreshToken = refreshToken
    )

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }
}
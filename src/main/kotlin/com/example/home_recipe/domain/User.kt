package com.example.home_recipe.domain

import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var refrigeratorId: Long? = null,

    @Column(nullable = false, length = 50, unique = true)
    var loginId: String,

    @Column(nullable = false, length = 255)
    var password: String,

    @Column(nullable = false, length = 100)
    var email: String,

    @Column(nullable = false, length = 11, unique = true)
    var phoneNumber: String
) {
    constructor(
        loginId: String,
        password: String,
        email: String,
        phoneNumber: String
    ) : this(
        id = null,
        refrigeratorId = null,
        loginId = loginId,
        password = password,
        email = email,
        phoneNumber = phoneNumber
    )

    fun assignRefrigerator(refrigeratorId: Long) {
        if (this.refrigeratorId != null) {
            throw IllegalStateException(
                "이미 refrigeratorId가 할당된 사용자입니다. (userId=$id)"
            )
        }
        this.refrigeratorId = refrigeratorId;
    }
}

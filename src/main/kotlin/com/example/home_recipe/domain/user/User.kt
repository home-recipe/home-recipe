package com.example.home_recipe.domain.user

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

    @Column(nullable = false, length = 10)
    var name: String,

    @Column(nullable = false, length = 100)
    var email: String,

    @Column(nullable = false, length = 11, unique = true)
    var phoneNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var role: Role = Role.USER
) {
    constructor(
        loginId: String,
        password: String,
        name: String,
        email: String,
        phoneNumber: String,
        role: Role = Role.USER
    ) : this(
        id = null,
        refrigeratorId = null,
        loginId = loginId,
        password = password,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        role = role
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

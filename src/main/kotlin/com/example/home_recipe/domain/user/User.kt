package com.example.home_recipe.domain.user

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = true)
    var refrigeratorId: Long? = null,

    @Column(nullable = false, length = 100)
    var email: String,

    @Column(nullable = false, length = 255)
    var password: String,

    @Column(nullable = false, length = 10)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var role: Role = Role.USER
) {
    constructor(
        password: String,
        name: String,
        email: String,
        role: Role = Role.USER
    ) : this(
        id = null,
        refrigeratorId = null,
        password = password,
        name = name,
        email = email,
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

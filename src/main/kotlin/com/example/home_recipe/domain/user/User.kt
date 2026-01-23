package com.example.home_recipe.domain.user

import com.example.home_recipe.domain.refrigerator.Refrigerator
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refrigerator_id")
    var refrigerator: Refrigerator? = null,

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
        name: String,
        password: String,
        email: String,
        role: Role = Role.USER
    ) : this(
        id = null,
        refrigerator = null,
        password = password,
        name = name,
        email = email,
        role = role
    )

    fun assignRefrigerator(refrigerator: Refrigerator) {
        if (this.refrigerator != null) {
            throw IllegalStateException(
                "이미 refrigeratorId가 할당된 사용자입니다. (userId=$id)"
            )
        }
        this.refrigerator = refrigerator;
    }

    fun hasRefrigerator(): Boolean = refrigerator != null

    fun updateRole(role: Role) : User {
        this.role = role;
        return this;
    }

    val refrigeratorExternal: Refrigerator
        get() = refrigerator
            ?: throw IllegalStateException(
                "아직 refrigerator 가 할당되지 않은 사용자입니다. (userId=$id)"
            )
}

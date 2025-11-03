package com.example.home_recipe.domain.user

import com.example.home_recipe.domain.refrigerator.Refrigerator
import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

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
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "refrigerator_id", unique = true)
    private var refrigerator: Refrigerator? = null

    val refrigeratorExternal: Refrigerator
        get() = refrigerator
            ?: error("할당된 냉장고가 없습니다. (userId=$id)")

    constructor(
        password: String,
        name: String,
        email: String,
        role: Role = Role.USER
    ) : this(
        id = null,
        password = password,
        name = name,
        email = email,
        role = role
    )

    fun hasRefrigerator(): Boolean = refrigerator != null

    fun assignRefrigerator(refrigerator: Refrigerator) {
        if (this.refrigerator != null) {
            error("이미 냉장고가 할당된 사용자입니다. (userId=$id)")
        }
        this.refrigerator = refrigerator
    }
}

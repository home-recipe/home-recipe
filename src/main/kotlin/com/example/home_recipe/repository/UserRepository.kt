package com.example.home_recipe.repository

import com.example.home_recipe.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun save(user: User) : User

    fun existsByLoginId(loginId: String) : Boolean
    fun existsByEmail(email: String) : Boolean
    fun existsByPhoneNumber(phoneNumber: String) : Boolean
}
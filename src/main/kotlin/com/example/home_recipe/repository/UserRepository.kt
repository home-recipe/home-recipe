package com.example.home_recipe.repository

import com.example.home_recipe.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByLoginId(loginId: String) : Boolean
    fun existsByEmail(email: String) : Boolean
    fun existsByPhoneNumber(phoneNumber: String) : Boolean

    fun findByLoginId(loginId: String) : Optional<User>
}
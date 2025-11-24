package com.example.home_recipe.repository

import com.example.home_recipe.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmail(email: String) : Boolean
    fun findByEmail(email: String) : Optional<User>
}
package com.example.home_recipe.repository

import com.example.home_recipe.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Optional<User>

    @Query("""
        select u
        from User u
        join fetch u.refrigerator
        where u.email = :email
    """)
    fun findByEmailWithRefrigerator(@Param("email") email: String) : Optional<User>
}
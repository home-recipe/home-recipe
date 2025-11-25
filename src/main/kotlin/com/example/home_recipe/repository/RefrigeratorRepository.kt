package com.example.home_recipe.repository;

import com.example.home_recipe.domain.refrigerator.Refrigerator
import com.example.home_recipe.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository;
import java.util.*

@Repository
interface RefrigeratorRepository : JpaRepository<Refrigerator, Long> {
}

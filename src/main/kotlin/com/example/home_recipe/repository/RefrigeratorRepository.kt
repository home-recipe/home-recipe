package com.example.home_recipe.repository;

import com.example.home_recipe.domain.refrigerator.Refrigerator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository;

@Repository
interface RefrigeratorRepository : JpaRepository<Refrigerator, Long> {
}

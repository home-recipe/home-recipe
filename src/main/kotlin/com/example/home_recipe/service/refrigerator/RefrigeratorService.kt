package com.example.home_recipe.service.refrigerator

import com.example.home_recipe.controller.dto.user.dto.UserJoinedEvent
import com.example.home_recipe.domain.refrigerator.Refrigerator
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.repository.RefrigeratorRepository
import com.example.home_recipe.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class RefrigeratorService(
    private val refrigeratorRepository: RefrigeratorRepository,
    private val ingredientRepository: IngredientRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createForUser(email: String): Refrigerator {
        val user = userRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("User not found: $email") }

        if (user.hasRefrigerator()) return user.refrigeratorExternal

        val fridge = refrigeratorRepository.save(Refrigerator.create())
        user.assignRefrigerator(fridge)
        return fridge
    }

    @Transactional
    fun addIngredient(email: String, ingredientId: Long): Boolean {
    }

    @Transactional
    fun useIngredient(email: String, ingredientId: Long): Boolean {
    }

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun onUserJoined(event: UserJoinedEvent) {
        val user = userRepository.findByEmail(event.email)
            .orElseThrow { BusinessException(UserCode.LOGIN_ERROR_002, HttpStatus.UNAUTHORIZED) }

        val fridge = refrigeratorRepository.save(Refrigerator.create())
        user.assignRefrigerator(fridge)
    }
}

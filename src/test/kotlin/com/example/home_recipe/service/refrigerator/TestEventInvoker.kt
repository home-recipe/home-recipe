package com.example.home_recipe.service.refrigerator

import com.example.home_recipe.controller.refrigerator.dto.UserJoinedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Service
class TestEventInvoker(
    private val publisher: ApplicationEventPublisher,
    txManager: PlatformTransactionManager,
) {
    private val txTemplate = TransactionTemplate(txManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    fun publishUserJoinedAndCommit(userId: Long, email: String) {
        val event = UserJoinedEvent(userId = userId, email = email)

        txTemplate.executeWithoutResult {
            publisher.publishEvent(event)
        }
    }
}
package com.example.home_recipe.service.refrigerator

import com.example.home_recipe.controller.refrigerator.dto.UserJoinedEvent
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class TestEventInvoker(
    private val publisher: ApplicationEventPublisher
) {
    @Transactional
    fun publishUserJoinedAndCommit(userId: Long, email: String) {
        // given
        val event = UserJoinedEvent(userId = userId, email = email)

        // when
        publisher.publishEvent(event)

        // then -> 커밋 시점에 리스너 동작 (BEFORE_COMMIT/AFTER_COMMIT 설정에 따라)
    }
}
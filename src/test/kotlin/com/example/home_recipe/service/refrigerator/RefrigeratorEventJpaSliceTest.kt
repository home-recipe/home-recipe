package com.example.home_recipe.service.refrigerator

import com.example.home_recipe.controller.refrigerator.dto.UserJoinedEvent
import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.refrigerator.Refrigerator
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.repository.RefreshTokenRepository
import com.example.home_recipe.repository.RefrigeratorRepository
import com.example.home_recipe.repository.UserRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@DataJpaTest(
    properties = [
        "spring.jpa.hibernate.ddl-auto=create"
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RefrigeratorService::class, TestEventInvoker::class, RefrigeratorEventJpaSliceTest.JpaTestConfig::class)
@ActiveProfiles("test")
class RefrigeratorEventJpaSliceTest {
    @TestConfiguration
    @EnableJpaRepositories(basePackageClasses = [
        UserRepository::class,
        RefrigeratorRepository::class,
        IngredientRepository::class
    ])
    @EntityScan(basePackageClasses = [
        User::class,
        Refrigerator::class,
        Ingredient::class
    ])
    class JpaTestConfig

    @Autowired lateinit var refrigeratorService: RefrigeratorService

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var refrigeratorRepository: RefrigeratorRepository
    @Autowired lateinit var invoker: TestEventInvoker

    @MockitoBean
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired lateinit var em: EntityManager

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("UserJoinedEvent 처리로 냉장고 생성 및 할당")
    fun userJoinedEvent_생성() {
        val user = userRepository.save(User(email = "e@e.com", password = "pw", name = "name"))
        invoker.publishUserJoinedAndCommit(user.id!!, user.email)

        em.clear()

        val reloaded = userRepository.findById(user.id!!).get()
        assertThat(reloaded.hasRefrigerator()).isTrue()
        val fridgeId = reloaded.refrigeratorExternal.id!!
        assertThat(refrigeratorRepository.findById(fridgeId)).isPresent
    }

    @Test
    @DisplayName("UserJoinedEvent - 이미 냉장고가 있으면 변경 없음(추가 생성 안 함)")
    fun userJoinedEvent_이미있음() {
        // given
        val user = userRepository.save(User(email = "a@b.com", password = "pw", name = "me"))
        val existing = refrigeratorRepository.save(Refrigerator.create())
        user.assignRefrigerator(existing)
        userRepository.save(user)

        val before = refrigeratorRepository.count()

        // when
        invoker.publishUserJoinedAndCommit(user.id!!, user.email)

        // then
        val reloaded = userRepository.findById(user.id!!).get()
        assertThat(reloaded.refrigeratorExternal.id).isEqualTo(existing.id)
        assertThat(refrigeratorRepository.count()).isEqualTo(before) // 새로 안 만들어졌는지 확인
    }

    @Test
    @DisplayName("UserJoinedEvent - 존재하지 않는 유저면 예외 발생")
    fun userJoinedEvent_유저없음() {
        // given
        val notExists = 999_999L
        val event = UserJoinedEvent(notExists, "none@example.com")

        // when & then
        assertThatThrownBy {
            refrigeratorService.onUserJoined(event)
        }.isInstanceOf(BusinessException::class.java)
    }
}

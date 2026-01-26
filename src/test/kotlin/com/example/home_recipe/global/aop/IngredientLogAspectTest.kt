package com.example.home_recipe.global.aop

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.example.home_recipe.domain.ingredient.IngredientCategory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.Test

@ActiveProfiles("test")
class IngredientLogAspectTest {

    private val aspect = IngredientLogAspect()

    @Test
    fun `재료 조회 시 결과가 있으면 로그가 정상적으로 남아야 한다`() {
        // Given
        val joinPoint = mockk<ProceedingJoinPoint>()
        val signature = mockk<MethodSignature>()
        val mockResponse = listOf(
            IngredientResponse(name = "당근", category = IngredientCategory.ETC, source = Source.DATABASE),
            IngredientResponse(name = "양파", category = IngredientCategory.ETC, source = Source.DATABASE)
        )

        every { joinPoint.signature } returns signature
        every { signature.name } returns "findIngredientsContainingName"
        every { joinPoint.args } returns arrayOf("당근")
        every { joinPoint.proceed() } returns Mono.just(mockResponse)

        // When
        val result = aspect.logIngredients(joinPoint) as Mono<*>

        // Then
        StepVerifier.create(result)
            .expectNext(mockResponse)
            .verifyComplete()

        // proceed() 호출 확인
        verify(exactly = 1) { joinPoint.proceed() }
    }
}
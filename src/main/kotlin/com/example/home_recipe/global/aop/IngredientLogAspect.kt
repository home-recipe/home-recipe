package com.example.home_recipe.global.aop

import com.example.home_recipe.controller.ingredient.dto.response.IngredientResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Aspect
@Component
class IngredientLogAspect {
    private val log = LoggerFactory.getLogger(javaClass)

    @Around("execution(* com.example.home_recipe.service.ingredient.IngredientService.findIngredientsContainingName(..))")
    fun logIngredients(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        val searchName = joinPoint.args.firstOrNull()

        log.info("조회 메서드: $methodName | 검색어: '$searchName' ")

        val result = joinPoint.proceed()

        if (result is Mono<*>) {
            return result.doOnSuccess { response ->
                if (response is List<*>) {
                    val ingredients = response.filterIsInstance<IngredientResponse>()

                    if (ingredients.isNotEmpty()) {
                        val source = ingredients.first().source
                        log.info("출처 : [$source] (총 ${ingredients.size}건)")

                        // 재료명 리스트 요약 출력
                        val names = ingredients.joinToString(", ") { it.name }
                        log.info("결과: $names")
                    } else {
                        log.warn("[$searchName]에 대한 결과가 DB와 API 모두 없음")
                    }
                }
            }
        }
        return result
    }

}
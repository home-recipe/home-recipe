package com.example.home_recipe.service.ingredient

import com.example.home_recipe.domain.ingredient.IngredientCategory
import org.springframework.stereotype.Component

@Component
class IngredientPromptGenerator {

    fun generate(userInput: String): String {
        val categories = IngredientCategory.entries.joinToString(", ")

        return """
            사용자가 입력한 단어: "$userInput"
            
            당신은 식재료 데이터 전문가입니다. 다음 지침에 따라 분석하세요:
            1. 사용자가 입력한 단어가 식재료인지 판단합니다.
            2. 사용자가 입력한 단어의 오타를 교정하고 대중적인 '표준 상품명'을 결정합니다.
            3. 카테고리는 반드시 아래 제공된 목록 중 하나만 선택해야 합니다.
            
            [중요: 카테고리 선택 목록]
            $categories
            
            응답은 반드시 다른 설명 없이 아래 JSON 형식으로만 출력하세요:
            {
              "isIngredient": true/false,
              "standardName": "표준 상품명 또는 교정된 이름",
              "category": "위 목록 중 정확히 일치하는 상숫값"
            }
        """.trimIndent()
    }
}
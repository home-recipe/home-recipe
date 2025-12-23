package com.example.home_recipe.domain.ingredient

data class BasicIngredients(
    val category: IngredientCategory,
    val name: String,
) {
    fun toEntity(): Ingredient = Ingredient(category, name)

    companion object {
        val EGG = BasicIngredients(
            category = IngredientCategory.ETC, // 혹은 MEAT 등 너 규칙에 맞게
            name = "계란"
        )

        val SOY_SAUCE = BasicIngredients(
            category = IngredientCategory.SPICE,
            name = "간장"
        )

        val SUGAR = BasicIngredients(
            category = IngredientCategory.SPICE,
            name = "설탕"
        )

        val RICE = BasicIngredients(
            category = IngredientCategory.GRAIN,
            name = "쌀"
        )

        val RAMEN = BasicIngredients(
            category = IngredientCategory.ETC,
            name = "라면"
        )

        val SPAM = BasicIngredients(
            category = IngredientCategory.MEAT,
            name = "스팸"
        )

        val TUNA_CAN = BasicIngredients(
            category = IngredientCategory.FISH,
            name = "참치캔"
        )

        val CABBAGE_KIMCHI = BasicIngredients(
            category = IngredientCategory.VEGETABLE,
            name = "배추김치"
        )

        val SEAWEED = BasicIngredients(
            category = IngredientCategory.ETC,
            name = "김"
        )

        val DEFAULTS: List<BasicIngredients> = listOf(
            EGG,
            SOY_SAUCE,
            RICE,
            RAMEN,
            SPAM,
            TUNA_CAN,
            CABBAGE_KIMCHI,
            SUGAR,
            SEAWEED
        )
    }
}


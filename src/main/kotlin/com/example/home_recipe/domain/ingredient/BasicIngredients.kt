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

        val RICE = BasicIngredients(
            category = IngredientCategory.GRAIN,
            name = "쌀"
        )

        val DEFAULTS: List<BasicIngredients> = listOf(
            EGG,
            SOY_SAUCE,
            RICE
        )
    }
}


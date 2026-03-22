package com.example.home_recipe.service.ingredient

enum class GuardrailResult { PASS, BLOCK, UNCERTAIN }

interface IngredientGuardrailStrategy {
    val name: String
    fun evaluate(foodSim: Double, nonFoodSim: Double): GuardrailResult
}

class SimpleThresholdStrategy(
    private val threshold: Double = 0.5
) : IngredientGuardrailStrategy {
    override val name = "SimpleThreshold(t=$threshold)"

    override fun evaluate(foodSim: Double, nonFoodSim: Double): GuardrailResult {
        return if (foodSim > threshold) GuardrailResult.PASS else GuardrailResult.BLOCK
    }
}

class DiffBasedStrategy(
    private val diffThreshold: Double = 0.05
) : IngredientGuardrailStrategy {
    override val name = "DiffBased(t=$diffThreshold)"

    override fun evaluate(foodSim: Double, nonFoodSim: Double): GuardrailResult {
        val diff = foodSim - nonFoodSim
        return when {
            diff > diffThreshold -> GuardrailResult.PASS
            diff < -diffThreshold -> GuardrailResult.BLOCK
            else -> GuardrailResult.UNCERTAIN
        }
    }
}

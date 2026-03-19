package com.example.home_recipe.service.ingredient

import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel
import dev.langchain4j.model.embedding.onnx.PoolingMode // 추가됨
import dev.langchain4j.store.embedding.CosineSimilarity
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class IngredientGuardrailService {

    private val log = LoggerFactory.getLogger(javaClass)

    private var embeddingModel: EmbeddingModel? = null
    private var foodAnchor: Embedding? = null
    private var nonFoodAnchor: Embedding? = null
    private var initialized = false

    @PostConstruct
    fun init() {
        try {
            val modelPath = Paths.get("src/main/resources/model/ko-sroberta.onnx").toString()
            val tokenizerPath = Paths.get("src/main/resources/model/tokenizer.json").toString()

            embeddingModel = OnnxEmbeddingModel(modelPath, tokenizerPath, PoolingMode.MEAN)

            foodAnchor = embeddingModel!!.embed("식재료 요리 재료 음식 채소 과일 육류 생선 곡물 양념 소스").content()
            nonFoodAnchor = embeddingModel!!.embed("신발 옷 가전 가구 생활잡화 도구 전자제품 스마트폰 자동차").content()

            initialized = true
            log.info("[Guardrail] 로컬 임베딩 엔진 로드 성공")
        } catch (e: Exception) {
            log.warn("[Guardrail] 엔진 로드 실패 - 가드레일 비활성화 (모든 입력 통과): {}", e.message)
        }
    }

    fun isIngredient(userInput: String): Boolean {
        if (!initialized) {
            log.warn("[Guardrail] 엔진 미초기화 - 입력 '{}' 통과 처리", userInput)
            return true
        }

        return try {
            val userEmbedding = embeddingModel!!.embed(userInput).content()
            val foodSim = CosineSimilarity.between(userEmbedding, foodAnchor!!)
            val nonFoodSim = CosineSimilarity.between(userEmbedding, nonFoodAnchor!!)

            log.info("[Guardrail] 입력: '{}' | 음식: {} | 비음식: {}",
                userInput, "%.4f".format(foodSim), "%.4f".format(nonFoodSim))

            // 1. [차단] 비음식이 압도적으로 높은 경우 (예: 2배 이상)
            if (nonFoodSim > foodSim * 2.0) {
                log.warn("[Guardrail] 확실한 비음식으로 판단하여 차단: '{}'", userInput)
                return false
            }
            // 2. [통과] 음식이 확실히 높은 경우
            if (foodSim > nonFoodSim && foodSim > 0.4) {
                log.info("[Guardrail] 로컬 모델 판단 통과: '{}'", userInput)
                return true
            }
            // 3. [LLM 위임] 판단이 어렵거나(점수 차이가 적음) 오타가 의심되는 경우
            log.info("[Guardrail] 판단 모호 - Gemini에게 최종 확인 요청: '{}'", userInput)
            return true

        } catch (e: Exception) {
            log.error("[Guardrail] 판별 오류: {}", e.message)
            true
        }
    }
}
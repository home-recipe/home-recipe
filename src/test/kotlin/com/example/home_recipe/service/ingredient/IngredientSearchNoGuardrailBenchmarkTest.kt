package com.example.home_recipe.service.ingredient

import com.example.home_recipe.HomeRecipeApplication
import com.example.home_recipe.controller.ingredient.dto.response.Source
import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientCategory
import com.example.home_recipe.global.config.S3Config
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.service.storage.S3ImageStorageService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [HomeRecipeApplication::class])
@ActiveProfiles("test")
class IngredientSearchNoGuardrailBenchmarkTest {

    @Autowired
    lateinit var searchService: IngredientSearchService

    @Autowired
    lateinit var ingredientRepository: IngredientRepository

    @MockBean
    lateinit var ingredientGuardrailService: IngredientGuardrailService

    @MockBean
    lateinit var s3ImageStorageService: S3ImageStorageService

    @MockBean
    lateinit var s3Config: S3Config

    private data class KeywordEntry(val keyword: String, val group: String)
    private data class ResultEntry(
        val keyword: String,
        val group: String,
        val layer: String,
        val elapsedMs: Long
    )

    private val keywords = listOf(
        // 확실한 식재료
        KeywordEntry("삼겹살", "확실한 식재료"),
        KeywordEntry("양파", "확실한 식재료"),
        KeywordEntry("고추장", "확실한 식재료"),
        KeywordEntry("마늘", "확실한 식재료"),
        KeywordEntry("감자", "확실한 식재료"),
        KeywordEntry("두부", "확실한 식재료"),
        KeywordEntry("계란", "확실한 식재료"),
        KeywordEntry("고등어", "확실한 식재료"),
        KeywordEntry("닭가슴살", "확실한 식재료"),
        KeywordEntry("간장", "확실한 식재료"),
        KeywordEntry("대파", "확실한 식재료"),
        KeywordEntry("시금치", "확실한 식재료"),
        KeywordEntry("우유", "확실한 식재료"),
        KeywordEntry("쌀", "확실한 식재료"),
        KeywordEntry("참기름", "확실한 식재료"),
        KeywordEntry("된장", "확실한 식재료"),
        KeywordEntry("치즈", "확실한 식재료"),
        KeywordEntry("새우", "확실한 식재료"),
        KeywordEntry("콩나물", "확실한 식재료"),
        KeywordEntry("무", "확실한 식재료"),
        KeywordEntry("소고기", "확실한 식재료"),
        KeywordEntry("돼지고기", "확실한 식재료"),
        KeywordEntry("닭볶음탕용 닭", "확실한 식재료"),
        KeywordEntry("베이컨", "확실한 식재료"),
        KeywordEntry("소시지", "확실한 식재료"),
        KeywordEntry("스팸", "확실한 식재료"),
        KeywordEntry("연어", "확실한 식재료"),
        KeywordEntry("참치", "확실한 식재료"),
        KeywordEntry("오징어", "확실한 식재료"),
        KeywordEntry("낙지", "확실한 식재료"),
        KeywordEntry("전복", "확실한 식재료"),
        KeywordEntry("바지락", "확실한 식재료"),
        KeywordEntry("애호박", "확실한 식재료"),
        KeywordEntry("가지", "확실한 식재료"),
        KeywordEntry("브로콜리", "확실한 식재료"),
        KeywordEntry("파프리카", "확실한 식재료"),
        KeywordEntry("양배추", "확실한 식재료"),
        KeywordEntry("청양고추", "확실한 식재료"),
        KeywordEntry("팽이버섯", "확실한 식재료"),
        KeywordEntry("표고버섯", "확실한 식재료"),
        KeywordEntry("새송이버섯", "확실한 식재료"),
        KeywordEntry("사과", "확실한 식재료"),
        KeywordEntry("바나나", "확실한 식재료"),
        KeywordEntry("포도", "확실한 식재료"),
        KeywordEntry("딸기", "확실한 식재료"),
        KeywordEntry("블루베리", "확실한 식재료"),
        KeywordEntry("망고", "확실한 식재료"),

        // 신조어/유행
        KeywordEntry("불닭소스", "신조어/유행"),
        KeywordEntry("두쫀쿠", "신조어/유행"),
        KeywordEntry("당근케잌", "신조어/유행"),
        KeywordEntry("스파게티 소스", "신조어/유행"),
        KeywordEntry("납작당면", "신조어/유행"),
        KeywordEntry("마라탕 소스", "신조어/유행"),
        KeywordEntry("로제떡볶이 소스", "신조어/유행"),
        KeywordEntry("마라로제", "신조어/유행"),
        KeywordEntry("두바이 초콜릿", "신조어/유행"),
        KeywordEntry("요아정 요거트", "신조어/유행"),
        KeywordEntry("아샷추", "신조어/유행"),
        KeywordEntry("탕후루 키트", "신조어/유행"),
        KeywordEntry("훠궈 홍탕", "신조어/유행"),
        KeywordEntry("분모자", "신조어/유행"),
        KeywordEntry("중국당면", "신조어/유행"),
        KeywordEntry("분짜 소스", "신조어/유행"),
        KeywordEntry("스리라차", "신조어/유행"),
        KeywordEntry("트러플 오일", "신조어/유행"),
        KeywordEntry("저당 굴소스", "신조어/유행"),
        KeywordEntry("알룰로스", "신조어/유행"),
        KeywordEntry("스테비아", "신조어/유행"),
        KeywordEntry("제로콜라", "신조어/유행"),
        KeywordEntry("오트밀크", "신조어/유행"),
        KeywordEntry("아몬드브리즈", "신조어/유행"),
        KeywordEntry("그릭요거트", "신조어/유행"),
        KeywordEntry("그래놀라", "신조어/유행"),
        KeywordEntry("단백질 쉐이크", "신조어/유행"),

        // 오타/변형
        KeywordEntry("카래", "오타/변형"),
        KeywordEntry("떢볶이", "오타/변형"),
        KeywordEntry("삼겹쌀", "오타/변형"),
        KeywordEntry("파스타묜", "오타/변형"),
        KeywordEntry("김치찌게", "오타/변형"),
        KeywordEntry("된장찌게", "오타/변형"),
        KeywordEntry("외사비", "오타/변형"),
        KeywordEntry("고추짱", "오타/변형"),
        KeywordEntry("마요내즈", "오타/변형"),
        KeywordEntry("케찹", "오타/변형"),
        KeywordEntry("꼬추장", "오타/변형"),
        KeywordEntry("된짱찌게", "오타/변형"),
        KeywordEntry("샴겹살", "오타/변형"),
        KeywordEntry("닭까슴살", "오타/변형"),
        KeywordEntry("계란말이용계란", "오타/변형"),
        KeywordEntry("양퐈", "오타/변형"),
        KeywordEntry("감좌", "오타/변형"),
        KeywordEntry("고구매", "오타/변형"),
        KeywordEntry("옥쑤수", "오타/변형"),
        KeywordEntry("스파게티소쓰", "오타/변형"),
        KeywordEntry("토마토소스으", "오타/변형"),
        KeywordEntry("마라탕쏘쓰", "오타/변형"),
        KeywordEntry("휘핑크림모", "오타/변형"),
        KeywordEntry("생크림무", "오타/변형"),
        KeywordEntry("바나나우유우", "오타/변형"),
        KeywordEntry("요거트으", "오타/변형"),
        KeywordEntry("치즈으", "오타/변형"),
        KeywordEntry("버터어", "오타/변형"),

        // 완전 노이즈 (14건)
        KeywordEntry("스마트폰", "완전 노이즈"),
        KeywordEntry("나이키 운동화", "완전 노이즈"),
        KeywordEntry("자바스크립트", "완전 노이즈"),
        KeywordEntry("모니터", "완전 노이즈"),
        KeywordEntry("키보드", "완전 노이즈"),
        KeywordEntry("에어컨", "완전 노이즈"),
        KeywordEntry("세탁기", "완전 노이즈"),
        KeywordEntry("아이폰", "완전 노이즈"),
        KeywordEntry("책상", "완전 노이즈"),
        KeywordEntry("의자", "완전 노이즈"),
        KeywordEntry("샤프", "완전 노이즈"),
        KeywordEntry("볼펜", "완전 노이즈"),
        KeywordEntry("텀블러", "완전 노이즈"),
        KeywordEntry("마우스", "완전 노이즈"),
        KeywordEntry("맥북 프로", "완전 노이즈"),
        KeywordEntry("갤럭시 탭", "완전 노이즈"),
        KeywordEntry("블루투스 이어폰", "완전 노이즈"),
        KeywordEntry("보조배터리", "완전 노이즈"),
        KeywordEntry("충전기 케이블", "완전 노이즈"),
        KeywordEntry("독서실 책상", "완전 노이즈"),
        KeywordEntry("수면 안대", "완전 노이즈"),
        KeywordEntry("요가 매트", "완전 노이즈"),
        KeywordEntry("덤벨 5kg", "완전 노이즈"),
        KeywordEntry("축구공", "완전 노이즈"),
        KeywordEntry("등산화", "완전 노이즈"),
        KeywordEntry("캠핑 의자", "완전 노이즈"),
        KeywordEntry("선글라스", "완전 노이즈"),
        KeywordEntry("반팔 티셔츠", "완전 노이즈"),
        KeywordEntry("청바지", "완전 노이즈"),
        KeywordEntry("슬리퍼", "완전 노이즈"),
        KeywordEntry("에코백", "완전 노이즈"),
        KeywordEntry("향수", "완전 노이즈"),
        KeywordEntry("립스틱", "완전 노이즈"),
        KeywordEntry("파운데이션", "완전 노이즈"),
        KeywordEntry("선크림", "완전 노이즈")
    )


    private val dbIngredients = listOf(
        "양파", "마늘", "감자", "두부", "계란", "고등어", "참기름", "된장",
        "치즈", "새우", "우유", "시금치", "대파", "간장", "콩나물", "무"
    )

    @BeforeEach
    fun setUp() {
        // 가드레일을 항상 통과시킨다 (= 가드레일 없는 대조군)
        `when`(ingredientGuardrailService.isIngredient(any())).thenReturn(true)

        ingredientRepository.deleteAll()
        dbIngredients.forEach { name ->
            ingredientRepository.save(Ingredient(IngredientCategory.ETC, name))
        }
    }

    @Test
    fun `가드레일 미적용 대조군 벤치마크`() {
        val results = mutableListOf<ResultEntry>()

        keywords.forEach { (keyword, group) ->
            val start = System.currentTimeMillis()
            try {
                val result = runBlocking { searchService.search(keyword) }
                val elapsed = System.currentTimeMillis() - start
                val layer = when (result.first().source) {
                    Source.DATABASE -> "DB"
                    Source.OPEN_API -> "공공데이터 API"
                    Source.GEMINI -> "Gemini"
                }
                results.add(ResultEntry(keyword, group, layer, elapsed))
            } catch (e: BusinessException) {
                val elapsed = System.currentTimeMillis() - start
                // Gemini가 "식재료 아님"으로 판정한 경우에도 BusinessException 발생
                results.add(ResultEntry(keyword, group, "Gemini(비식재료 판정)", elapsed))
            } catch (e: Exception) {
                val elapsed = System.currentTimeMillis() - start
                results.add(ResultEntry(keyword, group, "에러(${e.javaClass.simpleName})", elapsed))
            }
        }

        printReport(results)
    }

    private fun printReport(results: List<ResultEntry>) {
        val total = results.size
        val layerOrder = listOf("가드레일 차단", "DB", "공공데이터 API", "Gemini", "Gemini(비식재료 판정)")
        val layerLabels = mapOf(
            "가드레일 차단" to "임베딩 차단 (Guardrail)",
            "DB" to "DB 조회 성공",
            "공공데이터 API" to "공공데이터 API 성공",
            "Gemini" to "AI(Gemini) 식재료 판정",
            "Gemini(비식재료 판정)" to "AI(Gemini) 비식재료 판정"
        )
        val layerCounts = results.groupingBy { it.layer }.eachCount()
        val errorLayers = layerCounts.keys.filter { it.startsWith("에러") }

        println()
        println("========================================")
        println("  가드레일 미적용 대조군 벤치마크 리포트")
        println("========================================")
        println()

        // --- 레이어별 집계 ---
        println("--- 레이어별 집계 ---")
        println("%-30s | %4s | %s".format("레이어", "건수", "비율"))
        println("-".repeat(56))
        layerOrder.forEach { layer ->
            val count = layerCounts.getOrDefault(layer, 0)
            val ratio = if (total > 0) count * 100.0 / total else 0.0
            println("%-30s | %4d | %5.1f%%".format(layerLabels[layer] ?: layer, count, ratio))
        }
        errorLayers.forEach { layer ->
            val count = layerCounts[layer]!!
            val ratio = count * 100.0 / total
            println("%-30s | %4d | %5.1f%%".format(layer, count, ratio))
        }
        println("-".repeat(56))

        val geminiTotal = layerCounts.getOrDefault("Gemini", 0) +
            layerCounts.getOrDefault("Gemini(비식재료 판정)", 0)
        println("%-30s | %4d | %5.1f%%".format(
            "AI(Gemini) 호출 합계", geminiTotal, if (total > 0) geminiTotal * 100.0 / total else 0.0))
        println("%-30s | %4d | %5.1f%%".format("합계", total, 100.0))
        println()

        // --- 그룹별 레이어 분포 ---
        val groups = listOf("확실한 식재료", "신조어/유행", "오타/변형", "완전 노이즈")
        val allLayers = layerOrder + errorLayers
        println("--- 그룹별 레이어 분포 ---")
        val shortLabels = listOf("차단", "  DB", " API", "  AI", "AI거부") +
            errorLayers.map { it.take(4) }
        println("%-16s | %s".format("그룹", shortLabels.joinToString(" | ") { "%5s".format(it) }))
        println("-".repeat(16 + 3 + allLayers.size * 8))
        groups.forEach { group ->
            val groupResults = results.filter { it.group == group }
            val counts = allLayers.map { layer -> groupResults.count { it.layer == layer } }
            println("%-16s | %s".format(group, counts.joinToString(" | ") { "%5d".format(it) }))
        }
        println()

        // --- 상세 결과 ---
        println("--- 상세 결과 ---")
        println("  %-14s | %-16s | %-24s | %s".format("키워드", "그룹", "레이어", "응답시간"))
        println("  " + "-".repeat(76))
        results.forEach { r ->
            println("  %-14s | %-16s | %-24s | %dms".format(r.keyword, r.group, r.layer, r.elapsedMs))
        }
        println()
    }
}

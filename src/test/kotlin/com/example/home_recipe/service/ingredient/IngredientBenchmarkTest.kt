package com.example.home_recipe.service.ingredient

import com.example.home_recipe.HomeRecipeApplication
import com.example.home_recipe.global.config.S3Config
import com.example.home_recipe.service.storage.S3ImageStorageService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [HomeRecipeApplication::class])
@ActiveProfiles("test")
class IngredientBenchmarkTest {

    @Autowired
    private lateinit var guardrailService: IngredientGuardrailService

    @MockBean
    private lateinit var s3ImageStorageService: S3ImageStorageService

    @MockBean
    private lateinit var s3Config: S3Config

    data class TestCase(val keyword: String, val group: String, val expected: GuardrailResult)

    private val testCases = listOf(
        // 그룹 1: 확실한 식재료 -> PASS
        TestCase("삼겹살", "확실한 식재료", GuardrailResult.PASS),
        TestCase("양파", "확실한 식재료", GuardrailResult.PASS),
        TestCase("고추장", "확실한 식재료", GuardrailResult.PASS),
        TestCase("마늘", "확실한 식재료", GuardrailResult.PASS),
        TestCase("감자", "확실한 식재료", GuardrailResult.PASS),
        TestCase("두부", "확실한 식재료", GuardrailResult.PASS),
        TestCase("계란", "확실한 식재료", GuardrailResult.PASS),
        TestCase("고등어", "확실한 식재료", GuardrailResult.PASS),
        TestCase("닭가슴살", "확실한 식재료", GuardrailResult.PASS),
        TestCase("간장", "확실한 식재료", GuardrailResult.PASS),
        TestCase("대파", "확실한 식재료", GuardrailResult.PASS),
        TestCase("시금치", "확실한 식재료", GuardrailResult.PASS),
        TestCase("우유", "확실한 식재료", GuardrailResult.PASS),
        TestCase("쌀", "확실한 식재료", GuardrailResult.PASS),
        TestCase("참기름", "확실한 식재료", GuardrailResult.PASS),
        TestCase("된장", "확실한 식재료", GuardrailResult.PASS),
        TestCase("치즈", "확실한 식재료", GuardrailResult.PASS),
        TestCase("새우", "확실한 식재료", GuardrailResult.PASS),
        TestCase("콩나물", "확실한 식재료", GuardrailResult.PASS),
        TestCase("무", "확실한 식재료", GuardrailResult.PASS),
        TestCase("소고기", "확실한 식재료", GuardrailResult.PASS),
        TestCase("돼지고기", "확실한 식재료", GuardrailResult.PASS),
        TestCase("닭볶음탕용 닭", "확실한 식재료", GuardrailResult.PASS),
        TestCase("베이컨", "확실한 식재료", GuardrailResult.PASS),
        TestCase("소시지", "확실한 식재료", GuardrailResult.PASS),
        TestCase("스팸", "확실한 식재료", GuardrailResult.PASS),
        TestCase("연어", "확실한 식재료", GuardrailResult.PASS),
        TestCase("참치", "확실한 식재료", GuardrailResult.PASS),
        TestCase("오징어", "확실한 식재료", GuardrailResult.PASS),
        TestCase("낙지", "확실한 식재료", GuardrailResult.PASS),
        TestCase("전복", "확실한 식재료", GuardrailResult.PASS),
        TestCase("바지락", "확실한 식재료", GuardrailResult.PASS),
        TestCase("애호박", "확실한 식재료", GuardrailResult.PASS),
        TestCase("가지", "확실한 식재료", GuardrailResult.PASS),
        TestCase("브로콜리", "확실한 식재료", GuardrailResult.PASS),
        TestCase("파프리카", "확실한 식재료", GuardrailResult.PASS),
        TestCase("양배추", "확실한 식재료", GuardrailResult.PASS),
        TestCase("청양고추", "확실한 식재료", GuardrailResult.PASS),
        TestCase("팽이버섯", "확실한 식재료", GuardrailResult.PASS),
        TestCase("표고버섯", "확실한 식재료", GuardrailResult.PASS),
        TestCase("새송이버섯", "확실한 식재료", GuardrailResult.PASS),
        TestCase("사과", "확실한 식재료", GuardrailResult.PASS),
        TestCase("바나나", "확실한 식재료", GuardrailResult.PASS),
        TestCase("포도", "확실한 식재료", GuardrailResult.PASS),
        TestCase("딸기", "확실한 식재료", GuardrailResult.PASS),
        TestCase("블루베리", "확실한 식재료", GuardrailResult.PASS),
        TestCase("망고", "확실한 식재료", GuardrailResult.PASS),

        // 그룹 2: 신조어/유행 재료 -> PASS
        TestCase("불닭소스", "신조어/유행", GuardrailResult.PASS),
        TestCase("두쫀쿠", "신조어/유행", GuardrailResult.PASS),
        TestCase("당근케잌", "신조어/유행", GuardrailResult.PASS),
        TestCase("스파게티 소스", "신조어/유행", GuardrailResult.PASS),
        TestCase("납작당면", "신조어/유행", GuardrailResult.PASS),
        TestCase("마라탕 소스", "신조어/유행", GuardrailResult.PASS),
        TestCase("로제떡볶이 소스", "신조어/유행", GuardrailResult.PASS),
        TestCase("마라로제", "신조어/유행", GuardrailResult.PASS),
        TestCase("두바이 초콜릿", "신조어/유행", GuardrailResult.PASS),
        TestCase("탕후루 키트", "신조어/유행", GuardrailResult.PASS),
        TestCase("훠궈 홍탕", "신조어/유행", GuardrailResult.PASS),
        TestCase("분짜 소스", "신조어/유행", GuardrailResult.PASS),
        TestCase("마라탕 소스", "신조어/유행", GuardrailResult.PASS),
        TestCase("요아정 요거트", "신조어/유행", GuardrailResult.PASS),
        TestCase("아샷추", "신조어/유행", GuardrailResult.PASS),
        TestCase("제로콜라", "신조어/유행", GuardrailResult.PASS),
        TestCase("오트밀크", "신조어/유행", GuardrailResult.PASS),
        TestCase("아몬드브리즈", "신조어/유행", GuardrailResult.PASS),
        TestCase("그릭요거트", "신조어/유행", GuardrailResult.PASS),
        TestCase("분모자", "신조어/유행", GuardrailResult.PASS),
        TestCase("중국당면", "신조어/유행", GuardrailResult.PASS),
        TestCase("스리라차", "신조어/유행", GuardrailResult.PASS),
        TestCase("트러플 오일", "신조어/유행", GuardrailResult.PASS),
        TestCase("저당 굴소스", "신조어/유행", GuardrailResult.PASS),
        TestCase("알룰로스", "신조어/유행", GuardrailResult.PASS),
        TestCase("스테비아", "신조어/유행", GuardrailResult.PASS),
        TestCase("그래놀라", "신조어/유행", GuardrailResult.PASS),
        TestCase("단백질 쉐이크", "신조어/유행", GuardrailResult.PASS),

        // 그룹 3: 오타/변형 -> PASS
        TestCase("카래", "오타/변형", GuardrailResult.PASS),
        TestCase("떢볶이", "오타/변형", GuardrailResult.PASS),
        TestCase("삼겹쌀", "오타/변형", GuardrailResult.PASS),
        TestCase("파스타묜", "오타/변형", GuardrailResult.PASS),
        TestCase("김치찌게", "오타/변형", GuardrailResult.PASS),
        TestCase("된장찌게", "오타/변형", GuardrailResult.PASS),
        TestCase("외사비", "오타/변형", GuardrailResult.PASS),
        TestCase("고추짱", "오타/변형", GuardrailResult.PASS),
        TestCase("마요내즈", "오타/변형", GuardrailResult.PASS),
        TestCase("케찹", "오타/변형", GuardrailResult.PASS),
        TestCase("꼬추장", "오타/변형", GuardrailResult.PASS),
        TestCase("된짱찌게", "오타/변형", GuardrailResult.PASS),
        TestCase("샴겹살", "오타/변형", GuardrailResult.PASS),
        TestCase("닭까슴살", "오타/변형", GuardrailResult.PASS),
        TestCase("계란말이용계란", "오타/변형", GuardrailResult.PASS),
        TestCase("양퐈", "오타/변형", GuardrailResult.PASS),
        TestCase("감좌", "오타/변형", GuardrailResult.PASS),
        TestCase("고구매", "오타/변형", GuardrailResult.PASS),
        TestCase("옥쑤수", "오타/변형", GuardrailResult.PASS),
        TestCase("스파게티소쓰", "오타/변형", GuardrailResult.PASS),
        TestCase("토마토소스으", "오타/변형", GuardrailResult.PASS),
        TestCase("마라탕쏘쓰", "오타/변형", GuardrailResult.PASS),
        TestCase("휘핑크림모", "오타/변형", GuardrailResult.PASS),
        TestCase("생크림무", "오타/변형", GuardrailResult.PASS),
        TestCase("바나나우유우", "오타/변형", GuardrailResult.PASS),
        TestCase("요거트으", "오타/변형", GuardrailResult.PASS),
        TestCase("치즈으", "오타/변형", GuardrailResult.PASS),
        TestCase("버터어", "오타/변형", GuardrailResult.PASS),

        // 그룹 4: 완전 노이즈 -> BLOCK
        TestCase("스마트폰", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("나이키 운동화", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("자바스크립트", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("모니터", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("키보드", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("에어컨", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("세탁기", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("아이폰", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("책상", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("의자", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("샤프", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("볼펜", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("텀블러", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("마우스", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("맥북 프로", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("갤럭시 탭", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("블루투스 이어폰", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("보조배터리", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("충전기 케이블", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("독서실 책상", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("수면 안대", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("요가 매트", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("캠핑 의자", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("에코백", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("덤벨 5kg", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("축구공", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("등산화", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("선글라스", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("반팔 티셔츠", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("청바지", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("슬리퍼", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("향수", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("립스틱", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("파운데이션", "완전 노이즈", GuardrailResult.BLOCK),
        TestCase("선크림", "완전 노이즈", GuardrailResult.BLOCK),
    )

    @Test
    fun `가드레일 전략 A-B 벤치마크 테스트`() {
        val strategies = listOf(
            SimpleThresholdStrategy(threshold = 0.5),
            DiffBasedStrategy(diffThreshold = 0.05)
        )

        // 1. 임베딩 계산 1회 + 전략별 결과 수집
        data class EvalRecord(
            val testCase: TestCase,
            val foodSim: Double,
            val nonFoodSim: Double,
            val results: MutableMap<String, GuardrailResult> = mutableMapOf()
        )

        val records = testCases.map { tc ->
            val eval = guardrailService.evaluate(tc.keyword, strategies[0])
            EvalRecord(tc, eval.foodSim, eval.nonFoodSim).also {
                it.results[strategies[0].name] = eval.result
            }
        }

        // 두 번째 전략은 이미 계산된 유사도 값으로 평가
        val strategyB = strategies[1]
        records.forEach { record ->
            record.results[strategyB.name] = strategyB.evaluate(record.foodSim, record.nonFoodSim)
        }

        // 2. 리포트 출력
        val groups = testCases.map { it.group }.distinct()

        println("\n${"=".repeat(80)}")
        println("  가드레일 전략 A/B 벤치마크 리포트")
        println("=".repeat(80))

        for (strategy in strategies) {
            val sName = strategy.name
            println("\n--- 전략: $sName ---")
            println("%-14s | %5s | %5s | %5s | %8s".format("그룹", "PASS", "BLOCK", "UNCRT", "정확도"))
            println("-".repeat(56))

            var totalCorrect = 0
            var totalCount = 0
            var falsePositive = 0
            var falseNegative = 0

            for (group in groups) {
                val groupRecords = records.filter { it.testCase.group == group }
                val passCount = groupRecords.count { it.results[sName]!! == GuardrailResult.PASS }
                val blockCount = groupRecords.count { it.results[sName]!! == GuardrailResult.BLOCK }
                val uncertainCount = groupRecords.count { it.results[sName]!! == GuardrailResult.UNCERTAIN }

                val correct = groupRecords.count { tc ->
                    effectiveResult(tc.results[sName]!!) == tc.testCase.expected
                }
                val accuracy = if (groupRecords.isNotEmpty()) correct.toDouble() / groupRecords.size * 100 else 0.0

                totalCorrect += correct
                totalCount += groupRecords.size

                // FP: 노이즈인데 통과시킨 경우
                if (group == "완전 노이즈") {
                    falsePositive += passCount
                }
                // FN: 식재료인데 차단한 경우
                if (group != "완전 노이즈") {
                    falseNegative += blockCount
                }

                println("%-14s | %5d | %5d | %5d | %6.1f%%".format(group, passCount, blockCount, uncertainCount, accuracy))
            }

            val totalAccuracy = if (totalCount > 0) totalCorrect.toDouble() / totalCount * 100 else 0.0
            println("-".repeat(56))
            println("%-14s | %43s".format("전체 정확도", "%6.1f%%".format(totalAccuracy)))
            println("%-14s | %43s".format("False Positive", "$falsePositive 건 (노이즈 통과)"))
            println("%-14s | %43s".format("False Negative", "$falseNegative 건 (식재료 차단)"))
        }

        // 3. 상세 결과 목록
        println("\n${"=".repeat(80)}")
        println("  상세 결과 목록")
        println("=".repeat(80))
        for (strategy in strategies) {
            val sName = strategy.name
            val matches = records.filter { effectiveResult(it.results[sName]!!) == it.testCase.expected }
            val mismatches = records.filter { effectiveResult(it.results[sName]!!) != it.testCase.expected }

            println("\n[$sName] 일치 ${matches.size}건:")
            matches.forEach { r ->
                println("  O %-12s | 기대: %-5s | 실제: %-10s | foodSim=%.4f nonFoodSim=%.4f".format(
                    r.testCase.keyword,
                    r.testCase.expected,
                    r.results[sName],
                    r.foodSim,
                    r.nonFoodSim
                ))
            }

            if (mismatches.isNotEmpty()) {
                println("\n[$sName] 불일치 ${mismatches.size}건:")
                mismatches.forEach { r ->
                    println("  X %-12s | 기대: %-5s | 실제: %-10s | foodSim=%.4f nonFoodSim=%.4f".format(
                        r.testCase.keyword,
                        r.testCase.expected,
                        r.results[sName],
                        r.foodSim,
                        r.nonFoodSim
                    ))
                }
            } else {
                println("\n[$sName] 불일치 없음 (100% 정확)")
            }
        }

        println("\n${"=".repeat(80)}")
    }

    /**
     * UNCERTAIN은 통과 처리 (현재 운영 로직과 동일)
     */
    private fun effectiveResult(result: GuardrailResult): GuardrailResult {
        return if (result == GuardrailResult.UNCERTAIN) GuardrailResult.PASS else result
    }
}

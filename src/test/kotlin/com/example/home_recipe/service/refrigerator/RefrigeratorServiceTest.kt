package com.example.home_recipe.service.refrigerator

import com.example.home_recipe.domain.ingredient.Ingredient
import com.example.home_recipe.domain.ingredient.IngredientCategory
import com.example.home_recipe.domain.refrigerator.Refrigerator
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.IngredientCode
import com.example.home_recipe.global.response.code.RefrigeratorCode
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.IngredientRepository
import com.example.home_recipe.repository.RefrigeratorRepository
import com.example.home_recipe.repository.UserRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.*
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockitoExtension::class)
class RefrigeratorServiceTest {

    @Mock
    private lateinit var refrigeratorRepository: RefrigeratorRepository

    @Mock
    private lateinit var ingredientRepository: IngredientRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var refrigeratorService: RefrigeratorService

    //////////////// 해피 테스트

    @Test
    @DisplayName("냉장고 생성 - 유저가 냉장고가 없으면 생성 후 할당")
    fun 냉장고_생성_성공() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")

        whenever(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user))
        whenever(ingredientRepository.findByCategoryAndName(any(), any()))
            .thenReturn(null)
        whenever(ingredientRepository.save(any<Ingredient>()))
            .thenAnswer { invocation ->
                invocation.getArgument<Ingredient>(0)
            }
        whenever(refrigeratorRepository.save(any<Refrigerator>()))
            .thenAnswer { invocation ->
                invocation.getArgument<Refrigerator>(0)
            }

        // when
        val fridge = refrigeratorService.createForUser(email)

        // then
        assertThat(user.hasRefrigerator()).isTrue()
        assertThat(fridge).isSameAs(user.refrigeratorExternal)

        verify(refrigeratorRepository, times(1)).save(any<Refrigerator>())
        verify(ingredientRepository, atLeastOnce()).save(any<Ingredient>())
    }

    @Test
    @DisplayName("냉장고의 재료 모두 가져오기")
    fun 냉장고_재료_모두_가져오기() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        val fridge = Refrigerator.create()
        ReflectionTestUtils.setField(fridge, "id", 1L)
        user.assignRefrigerator(fridge)

        // 기본 재료 엔티티 mocking
        val egg = Ingredient(IngredientCategory.ETC, "계란").apply { id = 1L }
        val rice = Ingredient(IngredientCategory.GRAIN, "쌀").apply { id = 2L }
        fridge.addIngredient(egg)
        fridge.addIngredient(rice)

        whenever(userRepository.findByEmailWithRefrigerator(email)).thenReturn(Optional.of(user))
        whenever(refrigeratorRepository.findByIdWithIngredients(anyLong()))
            .thenReturn(Optional.of(fridge))

        // when
        val response = refrigeratorService.getMyIngredients(email)

        // then
        // 재료가 모두 포함되었는지
        Assertions.assertThat(response.myRefrigerator)
            .extracting<String> { ingredient -> ingredient.name }
            .containsExactlyInAnyOrder("계란", "쌀")
    }


    @Test
    @DisplayName("재료 추가 - 정상 추가 시 true")
    fun 재료_추가_성공() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        val fridge = Refrigerator.create()
        user.assignRefrigerator(fridge)

        val ingredient = Ingredient(IngredientCategory.VEGETABLE, "양파").apply { id = 10L }

        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))
        whenever(ingredientRepository.findById(10L)).thenReturn(Optional.of(ingredient))

        // when
        val added = refrigeratorService.addIngredient(email, 10L)

        // then
        Assertions.assertThat(added).isTrue()
        Assertions.assertThat(fridge.ingredients.any { it.id == 10L }).isTrue()
        verify(ingredientRepository, times(1)).findById(10L)
    }

    @Test
    @DisplayName("재료 사용 - 존재하면 제거되고 true")
    fun 재료_사용_성공() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        val fridge = Refrigerator.create()
        user.assignRefrigerator(fridge)

        val ingredient = Ingredient(IngredientCategory.VEGETABLE, "양파").apply { id = 10L }
        fridge.addIngredient(ingredient)

        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        // when
        val removed = refrigeratorService.useIngredient(email, 10L)

        // then
        Assertions.assertThat(removed).isTrue()
        Assertions.assertThat(fridge.ingredients.any { it.id == 10L }).isFalse()
    }

    @Test
    @DisplayName("냉장고 생성 - 기본 재료(계란/간장/쌀)가 자동으로 추가된다")
    fun 냉장고_생성_기본재료_자동추가() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        // 기본 재료 엔티티 mocking
        val egg = Ingredient(IngredientCategory.ETC, "계란").apply { id = 1L }
        val soySauce = Ingredient(IngredientCategory.SPICE, "간장").apply { id = 2L }
        val rice = Ingredient(IngredientCategory.GRAIN, "쌀").apply { id = 3L }

        whenever(
            ingredientRepository.findByCategoryAndName(IngredientCategory.ETC, "계란")
        ).thenReturn(egg)

        whenever(
            ingredientRepository.findByCategoryAndName(IngredientCategory.SPICE, "간장")
        ).thenReturn(soySauce)

        whenever(
            ingredientRepository.findByCategoryAndName(IngredientCategory.GRAIN, "쌀")
        ).thenReturn(rice)

        // 냉장고 저장 시, 그대로 인자로 들어온 객체를 반환하도록 설정
        doAnswer { inv -> inv.getArgument<Refrigerator>(0) }
            .whenever(refrigeratorRepository)
            .save(any<Refrigerator>())

        // when
        val fridge = refrigeratorService.createForUser(email)

        // then
        // 1) 유저에 냉장고가 할당됐는지
        Assertions.assertThat(user.hasRefrigerator()).isTrue()
        Assertions.assertThat(fridge).isSameAs(user.refrigeratorExternal)

        // 2) 기본 재료 이름이 다 들어가 있는지
        Assertions.assertThat(fridge.ingredients)
            .extracting<String> { it.name }
            .containsExactlyInAnyOrder("계란", "간장", "쌀")

        // 3) repository 메서드가 실제로 호출됐는지
        verify(ingredientRepository, times(1))
            .findByCategoryAndName(IngredientCategory.ETC, "계란")
        verify(ingredientRepository, times(1))
            .findByCategoryAndName(IngredientCategory.SPICE, "간장")
        verify(ingredientRepository, times(1))
            .findByCategoryAndName(IngredientCategory.GRAIN, "쌀")

        // 이 시나리오에서는 이미 DB에 있는 재료라고 가정했으니 save는 안 불려야 함
        verify(ingredientRepository, never()).save(any<Ingredient>())
    }



    //////////////// 예외/엣지 테스트

    @Test
    @DisplayName("냉장고 생성 - 이미 있으면 저장 호출 없이 기존 냉장고 반환")
    fun 냉장고_이미있음() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        val existing = Refrigerator.create()
        user.assignRefrigerator(existing)
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        // when
        val result = refrigeratorService.createForUser(email)

        // then
        Assertions.assertThat(result).isSameAs(existing)
        verify(refrigeratorRepository, times(0)).save(any())
    }

    @Test
    @DisplayName("재료 추가 - 이미 냉장고에 있으면 REFRIGERATOR_ERROR_005")
    fun 재료_추가_중복_예외() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        val fridge = Refrigerator.create()
        user.assignRefrigerator(fridge)
        val ingredient = Ingredient(IngredientCategory.VEGETABLE, "양파").apply { id = 10L }
        fridge.addIngredient(ingredient) // 이미 보유한 상태
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))
        whenever(ingredientRepository.findById(10L)).thenReturn(Optional.of(ingredient)) // 같은 인스턴스 반환

        // when & then
        assertThatThrownBy {
            refrigeratorService.addIngredient(email, 10L)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(RefrigeratorCode.REFRIGERATOR_ERROR_005.message)
    }

    @Test
    @DisplayName("재료 추가 - 재료가 없으면 INGREDIENT_ERROR_011")
    fun 재료_추가_재료없음() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        val fridge = Refrigerator.create()
        user.assignRefrigerator(fridge)
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))
        whenever(ingredientRepository.findById(999L)).thenReturn(Optional.empty())

        // when & then
        assertThatThrownBy {
            refrigeratorService.addIngredient(email, 999L)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(IngredientCode.INGREDIENT_ERROR_011.message)
    }

    @Test
    @DisplayName("재료 추가 - 냉장고가 없으면 REFRIGERATOR_ERROR_004")
    fun 재료_추가_냉장고없음() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name") // 냉장고 미할당
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        // when & then
        assertThatThrownBy {
            refrigeratorService.addIngredient(email, 1L)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(RefrigeratorCode.REFRIGERATOR_ERROR_004.message)
    }

    @Test
    @DisplayName("재료 사용 - 냉장고가 없으면 REFRIGERATOR_ERROR_004")
    fun 재료_사용_냉장고없음() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name") // 냉장고 미할당
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        // when & then
        assertThatThrownBy {
            refrigeratorService.useIngredient(email, 1L)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(RefrigeratorCode.REFRIGERATOR_ERROR_004.message)
    }

    @Test
    @DisplayName("재료 사용 - 없으면 false")
    fun 재료_사용_없음_false() {
        // given
        val email = "test@example.com"
        val user = User(email = email, password = "encoded", name = "name")
        val fridge = Refrigerator.create()
        user.assignRefrigerator(fridge)
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.of(user))

        // when
        val removed = refrigeratorService.useIngredient(email, 12345L)

        // then
        Assertions.assertThat(removed).isFalse()
    }

    @Test
    @DisplayName("유저가 없으면 LOGIN_ERROR_002")
    fun 유저없음_예외() {
        // given
        val email = "notfound@example.com"
        whenever(userRepository.findByEmail(email)).thenReturn(Optional.empty())

        // when & then
        assertThatThrownBy {
            refrigeratorService.createForUser(email)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessageContaining(UserCode.LOGIN_ERROR_002.message)
    }
}
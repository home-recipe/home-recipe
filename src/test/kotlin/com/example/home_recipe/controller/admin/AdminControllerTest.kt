package com.example.home_recipe.controller.admin

import com.example.home_recipe.controller.admin.request.UpdateRoleRequest
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.domain.user.User
import com.example.home_recipe.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.transaction.Transactional
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    private fun getAuth(email: String): JwtAuthenticationToken {
        userRepository.save(User(email = email, password = "pw", name = "me"))

        val authorities = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        val jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(email)
            .claim("email", email)
            .build()

        return JwtAuthenticationToken(jwt, authorities, email)
    }

    @Test
    @DisplayName("사용자 조회하기 성공")
    fun getAllUser_success() {
        // given
        val email = "create1@example.com"
        val auth = getAuth(email)

        userRepository.save(User("user1", "pw1", "user1@naver.com"))
        userRepository.save(User("user2", "pw2", "user2@naver.com"))
        userRepository.save(User("user3", "pw3", "user3@naver.com"))

        // 2. When & Then: 호출 및 검증F
        mockMvc.perform(
            get("/api/admin/users")
                .with(authentication(auth))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.data.length()").value(4))
            .andExpect(jsonPath("$.response.data[0].role").value("USER"))
    }

    @Test
    @DisplayName("사용자 권한 변경하기 성공")
    fun updateUserRole_success() {
        // given
        val email = "create1@example.com"
        val auth = getAuth(email)

        userRepository.save(User("user1", "pw1", "user1@naver.com"))
        val testUser = userRepository.save(User("user2", "pw2", "user2@naver.com"))
        userRepository.save(User("user3", "pw3", "user3@naver.com"))

        val request = UpdateRoleRequest(requireNotNull(testUser.id), Role.ADMIN)

        // 2. When & Then: 호출 및 검증
        mockMvc.perform(
            put("/api/admin/role")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.data.name").value("user2"))
            .andExpect(jsonPath("$.response.data.role").value("ADMIN"))
    }

    private fun toJson(obj: Any): String =
        ObjectMapper().registerKotlinModule().writeValueAsString(obj)
}
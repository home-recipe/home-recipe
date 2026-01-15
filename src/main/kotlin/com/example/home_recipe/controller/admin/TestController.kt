package com.example.home_recipe.controller.admin

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/me")
    fun getMyRole(authentication: Authentication): Map<String, Any> {
        return mapOf(
            "name" to authentication.name,
            "authorities" to authentication.authorities.map { it.authority }, // 권한 목록
            "isAuthenticated" to authentication.isAuthenticated
        )
    }
}
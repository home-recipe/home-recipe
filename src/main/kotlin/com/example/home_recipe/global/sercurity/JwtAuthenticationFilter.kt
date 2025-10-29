package com.example.home_recipe.global.sercurity

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ResponseCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter (
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            val responseCode = jwtProvider.validateAccessToken(token)

            if(responseCode == ResponseCode.AUTH_SUCCESS) {
                val email = jwtProvider.getEmailFromToken(token)
                val auth = UsernamePasswordAuthenticationToken(email, null, emptyList())
                SecurityContextHolder.getContext().authentication = auth
            } else {
                throw BusinessException(responseCode, HttpStatus.UNAUTHORIZED)
            }
        }
        filterChain.doFilter(request, response)
    }
}
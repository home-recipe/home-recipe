package com.example.home_recipe.controller.user.dto.response

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class EmailPrincipal(
    private val email: String
) : UserDetails {

    override fun getUsername(): String = email
    override fun getPassword(): String = ""
    override fun getAuthorities() = emptyList<GrantedAuthority>()
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}

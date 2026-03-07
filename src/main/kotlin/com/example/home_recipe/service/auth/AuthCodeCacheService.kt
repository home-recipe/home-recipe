package com.example.home_recipe.service.auth

import com.example.home_recipe.domain.auth.oauth2.AuthCodeEntry
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class AuthCodeCacheService(
    private val cacheManager: CacheManager,
) {

    @CachePut(value = ["authCodes"], key = "#code")
    fun store(code: String, entry: AuthCodeEntry): AuthCodeEntry {
        return entry
    }

    @Cacheable(value = ["authCodes"], key = "#code")
    fun find(code: String): AuthCodeEntry? {
        return null
    }

    fun evict(code: String) {
        cacheManager.getCache("authCodes")?.evict(code)
    }
}

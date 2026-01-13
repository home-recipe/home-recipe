package com.example.home_recipe.service.auth

import com.example.home_recipe.controller.admin.response.ManageResponseAssembler
import com.example.home_recipe.controller.admin.response.ManageUserResponse
import com.example.home_recipe.domain.user.Role
import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.UserCode
import com.example.home_recipe.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ManageService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getAllUsers(): List<ManageUserResponse> {
        return ManageResponseAssembler.toUsersResponse(userRepository.findAll())
    }

    @Transactional
    fun updateUserRole(id: Long, role: Role): ManageUserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { BusinessException(UserCode.LOGIN_ERROR_002, HttpStatus.UNAUTHORIZED) }
        return ManageResponseAssembler.toUserResponse(user.updateRole(role))
    }


}
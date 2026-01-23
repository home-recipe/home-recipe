package com.example.home_recipe.controller.admin

import com.example.home_recipe.controller.admin.request.UpdateRoleRequest
import com.example.home_recipe.controller.admin.response.AdminUserResponse
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.AuthCode
import com.example.home_recipe.service.admin.AdminService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService
) {

    @GetMapping("/users")
    fun getAllUsers(authentication: Authentication): ResponseEntity<ApiResponse<List<AdminUserResponse>>> {
        return ApiResponse.success(adminService.getAllUsers(), AuthCode.MANAGE_SUCCESS_001, HttpStatus.OK)
    }

    @PutMapping("/role")
    fun updateUserRole(
        authentication: Authentication,
        @Valid @RequestBody updateRoleRequest: UpdateRoleRequest
    ): ResponseEntity<ApiResponse<AdminUserResponse>> {
        val result = adminService.updateUserRole(updateRoleRequest.id, updateRoleRequest.role)
        return ApiResponse.success(result, AuthCode.MANAGE_SUCCESS_002, HttpStatus.OK)
    }
}
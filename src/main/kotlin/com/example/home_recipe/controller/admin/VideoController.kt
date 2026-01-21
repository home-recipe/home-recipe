package com.example.home_recipe.controller.admin

import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.VideoCode
import com.example.home_recipe.service.admin.VideoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/admin")
class VideoController(
    private val videoService: VideoService
) {

    @GetMapping
    fun getRandomVideos(authentication: Authentication): ResponseEntity<ApiResponse<List<String>>> {
        return ApiResponse.success(videoService.getRandomVideos(), VideoCode.VIDEO_003, HttpStatus.OK)
    }

    @PostMapping("/video")
    fun saveVideo(
        authentication: Authentication,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<Void>> {

        videoService.saveVideo(file)
        return ApiResponse.success(null, VideoCode.VIDEO_003, HttpStatus.OK)
    }

    @DeleteMapping("/video")
    fun deleteVideo(
        authentication: Authentication,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<Void>> {

        videoService.deleteVideo(file)
        return ApiResponse.success(null, VideoCode.VIDEO_003, HttpStatus.OK)
    }
}
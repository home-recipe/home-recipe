package com.example.home_recipe.controller.admin

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.ApiResponse
import com.example.home_recipe.global.response.code.VideoCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream

@RestController
@RequestMapping("/api/videos")
class VideoController {

    private val videoPath = "/Users/mikyeong/home_recipe_assets/videos"

    @GetMapping
    fun getRandomVideos(authentication: Authentication): ResponseEntity<ApiResponse<List<String>>> {
        val directory = File(videoPath)

        if (!directory.exists()) {
            return ApiResponse.success(emptyList(), VideoCode.VIDEO_003, HttpStatus.OK)
        }
        val result = directory.listFiles { _, name ->
            val extension = name.lowercase()
            extension.endsWith(".mp4") || extension.endsWith(".mov")
        }?.map {
            "http://localhost:8080/videos/${it.name}"
        }?.shuffled() ?: emptyList()

        return ApiResponse.success(result, VideoCode.VIDEO_003, HttpStatus.OK)
    }

    @PostMapping("/video")
    fun saveVideo(
        authentication: Authentication,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<Void>> {

        if (file.isEmpty) {
            throw BusinessException(VideoCode.VIDEO_001, HttpStatus.BAD_REQUEST)
        }

        try {
            val destFile = File("$videoPath/${file.originalFilename}")

            file.inputStream.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            return ApiResponse.success(null, VideoCode.VIDEO_003, HttpStatus.OK)
        } catch (e: Exception) {
            throw BusinessException(VideoCode.VIDEO_002, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/video")
    fun deleteVideo(
        authentication: Authentication,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<Void>> {
        val destFile = File("$videoPath/${file.originalFilename}")

        if (file.isEmpty) {
            throw BusinessException(VideoCode.VIDEO_001, HttpStatus.NOT_FOUND)
        }

        val isDeleted = destFile.delete()

        if (!isDeleted) {
            throw BusinessException(VideoCode.VIDEO_004, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return ApiResponse.success(null, VideoCode.VIDEO_003, HttpStatus.OK)
    }
}
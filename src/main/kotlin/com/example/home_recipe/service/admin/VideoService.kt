package com.example.home_recipe.service.admin

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.VideoCode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream

@Service
class VideoService {

    private val videoPath = "/Users/mikyeong/home_recipe_assets/videos"

    fun getRandomVideos(): List<String> {
        val directory = File(videoPath)

        if (!directory.exists()) {
            return emptyList()
        }

        return directory.listFiles { _, name ->
            val extension = name.lowercase()
            extension.endsWith(".mp4") || extension.endsWith(".mov")
        }?.map {
            "http://localhost:8080/videos/${it.name}"
        }?.shuffled() ?: emptyList()
    }

    fun saveVideo(file: MultipartFile): Boolean {
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
            return true
        } catch (e: Exception) {
            throw BusinessException(VideoCode.VIDEO_002, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    fun deleteVideo(file: MultipartFile): Boolean {
        val destFile = File("$videoPath/${file.originalFilename}")

        if (file.isEmpty) {
            throw BusinessException(VideoCode.VIDEO_001, HttpStatus.NOT_FOUND)
        }

        val isDeleted = destFile.delete()

        if (!isDeleted) {
            throw BusinessException(VideoCode.VIDEO_004, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return true
    }
}
package com.example.home_recipe.service.storage

interface ImageStorageService {

    fun upload(imageBytes: ByteArray, fileName: String, contentType: String): String
}

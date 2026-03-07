package com.example.home_recipe.service.storage

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class S3ImageStorageService(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}") private val bucketName: String
) : ImageStorageService {

    private val log = LoggerFactory.getLogger(S3ImageStorageService::class.java)

    override fun upload(imageBytes: ByteArray, fileName: String, contentType: String): String {
        val key = "recipes/$fileName"

        val putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()

        s3Client.putObject(putRequest, RequestBody.fromBytes(imageBytes))

        val url = "https://$bucketName.s3.ap-northeast-2.amazonaws.com/$key"
        log.info("S3 이미지 업로드 완료: {}", url)
        return url
    }
}

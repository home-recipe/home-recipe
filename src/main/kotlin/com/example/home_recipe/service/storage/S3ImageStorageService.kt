package com.example.home_recipe.service.storage

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class S3ImageStorageService : ImageStorageService {

    private val log = LoggerFactory.getLogger(S3ImageStorageService::class.java)

    override fun upload(imageBytes: ByteArray, fileName: String, contentType: String): String {
        // TODO: 실제 S3 업로드 로직 구현
        // val putRequest = PutObjectRequest.builder()
        //     .bucket(bucketName)
        //     .key("recipes/$fileName")
        //     .contentType(contentType)
        //     .build()
        // s3Client.putObject(putRequest, RequestBody.fromBytes(imageBytes))
        // return "https://$bucketName.s3.ap-northeast-2.amazonaws.com/recipes/$fileName"

        log.warn("S3 업로드가 아직 구현되지 않았습니다. placeholder URL을 반환합니다. fileName={}", fileName)
        return "https://placeholder-bucket.s3.ap-northeast-2.amazonaws.com/recipes/$fileName"
    }
}

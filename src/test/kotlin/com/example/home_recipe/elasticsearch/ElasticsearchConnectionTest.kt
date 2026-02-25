package com.example.home_recipe.elasticsearch

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.Request
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Elasticsearch 연결 확인 테스트
 * - Docker로 ES가 실행 중인 상태에서 테스트해야 합니다.
 * - docker compose up elasticsearch -d
 */
class ElasticsearchConnectionTest {

    /** Elasticsearch 클러스터에 정상 연결되는지 확인 */
    @Test
    fun `Elasticsearch 연결 테스트`() {
        val restClient = RestClient.builder(HttpHost("localhost", 9200, "http")).build()

        val response = restClient.performRequest(Request("GET", "/_cluster/health"))
        val statusCode = response.statusLine.statusCode
        val body = response.entity.content.bufferedReader().readText()

        println("Status code: $statusCode")
        println("Response: $body")

        assertEquals(200, statusCode, "Elasticsearch 클러스터 헬스체크 응답이 200이어야 합니다")

        restClient.close()
    }
}

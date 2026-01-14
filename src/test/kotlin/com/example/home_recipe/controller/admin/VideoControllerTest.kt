package com.example.home_recipe.controller.admin

import com.example.home_recipe.repository.UserRepository
import com.example.home_recipe.service.admin.VideoService
import jakarta.transaction.Transactional
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File
import java.nio.file.Path

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VideoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @TempDir
    private lateinit var tempDir: Path

    @Autowired
    private lateinit var controller: VideoController

    @Autowired
    private lateinit var videoService: VideoService

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(videoService, "videoPath", tempDir.toString())
    }


    @Test
    @WithMockUser
    @DisplayName("영상 업로드 테스트 성공")
    fun 영상_업로드_테스트_성공() {
        val file = MockMultipartFile("file", "test.mp4", "video/mp4", "test.data".toByteArray())

        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/videos/video").file(file)
        )
            .andExpect(status().isOk)

        assert(File(tempDir.toFile(), "test.mp4").exists())
    }

    @Test
    @WithMockUser
    @DisplayName("영상 업로드 테스트 - 파일이 비었을 경우 실패")
    fun 영상_업로드_테스트_파일_비었을_때_실패() {
        val emptyFile = MockMultipartFile("file", "", "video/mp4", "".toByteArray())

        mockMvc.perform(multipart("/api/videos/video").file(emptyFile))
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    @DisplayName("랜덤 영상 목록 조회 테스트")
    fun 랜덤_영상_목록_조회_테스트() {
        File(tempDir.toFile(), "sample1.mp4").createNewFile()
        File(tempDir.toFile(), "sample2.mov").createNewFile()

        mockMvc.perform(get("/api/videos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.response.data.length()").value(2))
            .andExpect(jsonPath("$.response.data[0]").value(containsString("http://localhost:8080/videos/sample2.mov")))
    }
}
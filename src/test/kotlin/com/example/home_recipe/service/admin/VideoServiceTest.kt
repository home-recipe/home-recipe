package com.example.home_recipe.service.admin

import com.example.home_recipe.global.exception.BusinessException
import com.example.home_recipe.global.response.code.VideoCode
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.ReflectionTestUtils
import java.io.File
import java.nio.file.Path

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VideoServiceTest {

    @Autowired
    private lateinit var videoService: VideoService

    @TempDir
    private lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(videoService, "videoPath", tempDir.toString())
    }

    @Test
    @WithMockUser
    @DisplayName("영상 업로드 테스트 성공")
    fun 영상_업로드_테스트_성공() {
        val file = MockMultipartFile("file", "test.mp4", "video/mp4", "test.data".toByteArray())

        videoService.saveVideo(file)

        assert(File(tempDir.toFile(), "test.mp4").exists())
    }

    @Test
    @WithMockUser
    @DisplayName("영상 업로드 테스트 - 파일이 비었을 경우 실패")
    fun 영상_업로드_테스트_파일_비었을_때_실패() {
        val emptyFile = MockMultipartFile("file", "", "video/mp4", "".toByteArray())

        val exception = assertThrows<BusinessException>{
            videoService.saveVideo(emptyFile)
        }

        assertThat(exception.baseCode).isEqualTo(VideoCode.VIDEO_001)
    }

    @Test
    @WithMockUser
    @DisplayName("랜덤 영상 목록 조회 테스트")
    fun 랜덤_영상_목록_조회_테스트() {
        File(tempDir.toFile(), "sample1.mp4").createNewFile()
        File(tempDir.toFile(), "sample2.mov").createNewFile()

        val result = videoService.getRandomVideos()

        Assertions.assertEquals(2, result.size)
    }
}
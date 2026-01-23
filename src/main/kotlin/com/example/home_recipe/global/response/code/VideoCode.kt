package com.example.home_recipe.global.response.code

enum class VideoCode(
    override val code: String,
    override val message: String
) : BaseCode {

    VIDEO_001("VIDEO_001", "파일이 비어있습니다."),
    VIDEO_002("VIDEO_002", "내부 서버 오류, 파일 저장 실패"),
    VIDEO_003("VIDEO_003", "요청 처리 성공"),
    VIDEO_004("VIDEO_004", "내부 서버 오류, 파일 삭제 실패")

}

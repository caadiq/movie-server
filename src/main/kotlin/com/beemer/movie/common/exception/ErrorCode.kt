package com.beemer.movie.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String?
) {
    /**
     * 400 Bad Request
     * 클라이언트가 잘못된 요청을 보냈을 때 발생하는 에러 코드
     * ex) 필수 요청 파라미터가 누락된 경우
     */
    INVALID_FIELD(HttpStatus.BAD_REQUEST, "잘못된 필드입니다."),


    /**
     * 401 Unauthorized
     * 클라이언트가 인증되지 않은 상태에서 보호된 리소스에 접근하려고 할 때 발생하는 에러 코드
     * ex) 로그인하지 않은 사용자가 로그인이 필요한 페이지에 접근할 때
     */


    /**
     * 404 Not Found
     * 클라이언트가 요청한 리소스를 찾을 수 없을 때 발생하는 에러 코드
     * ex) 존재하지 않는 데이터를 조회할 때
     */
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "영화를 찾을 수 없습니다."),


    /**
     * 500 Internal Server Error
     * 서버에 오류가 발생했을 때 발생하는 에러 코드
     * ex) 서버에서 처리되지 않은 예외가 발생했을 때
     */
}
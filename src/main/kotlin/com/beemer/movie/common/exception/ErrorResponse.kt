package com.beemer.movie.common.exception

import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?
) {
    companion object {
        fun responseEntity(errorCode: ErrorCode): ResponseEntity<ErrorResponse> {
            return ResponseEntity
                .status(errorCode.httpStatus)
                .body(
                    ErrorResponse(
                        status = errorCode.httpStatus.value(),
                        error = errorCode.httpStatus.name,
                        message = errorCode.message
                    )
                )
        }

        fun responseEntity(errorCode: ErrorCode, message: String?): ResponseEntity<ErrorResponse> {
            return ResponseEntity
                .status(errorCode.httpStatus)
                .body(
                    ErrorResponse(
                        status = errorCode.httpStatus.value(),
                        error = errorCode.httpStatus.name,
                        message = message
                    )
                )
        }
    }
}
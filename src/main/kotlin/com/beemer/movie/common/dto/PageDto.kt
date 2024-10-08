package com.beemer.movie.common.dto

data class PageDto(
    val previousPage: Int?,
    val currentPage: Int,
    val nextPage: Int?,
)
package com.beemer.movie.movie.dto

import java.time.LocalDate

data class ReleaseListDto(
    val movieCode: String,
    val movieName: String,
    val posterUrl: String,
    val releaseDate: LocalDate?
)
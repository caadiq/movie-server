package com.beemer.movie.movie.dto

import java.time.LocalDate

class MovieDetailsDto(
    val movieCode: String,
    val movieName: String,
    val movieNameEn: String?,
    val openDate: LocalDate?,
    val posterUrl: String?,
    val genres: List<String>,
    val runTime: Int?,
    val nation: String?,
    val grade: String?,
    val plot: String?
)
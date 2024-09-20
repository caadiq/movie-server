package com.beemer.movie.movie.dto

import java.time.LocalDate

data class WeeklyRankListDto(
    val movieCode: String,
    val movieName: String,
    val posterUrl: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val rank: Int,
    val rankIncrement: Int,
    val audiCount: Int,
    val audiIncrement: Int,
    val audiAccumulate: Int
)
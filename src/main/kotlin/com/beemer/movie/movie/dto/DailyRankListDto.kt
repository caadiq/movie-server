package com.beemer.movie.movie.dto

import java.time.LocalDate

data class DailyRankListDto(
    val movieCode: String,
    val movieName: String,
    val posterUrl: String,
    val date: LocalDate,
    val rank: Int,
    val rankIncrement: Int,
    val audiCount: Int,
    val audiIncrement: Int,
    val audiAccumulate: Int
)
package com.beemer.movie.movie.dto

import java.time.LocalDate

data class RankListDto(
    val movieCode: String,
    val movieName: String,
    val genre: String,
    val posterUrl: String,
    val openDate: LocalDate?,
    val rank: Int,
    val rankIncrement: Int,
    val audiCount: Int,
    val audiIncrement: Int,
    val audiAccumulate: Int
)
package com.beemer.movie.movie.dto

data class KobisMovieDetailsDto(
    val movieInfoResult: MovieInfoResult
)

data class MovieInfoResult(
    val movieInfo: MovieInfo,
    val source: String
)

data class MovieInfo(
    val movieCd: String,
    val movieNm: String,
    val movieNmEn: String,
    val showTm: String,
    val prdtYear: String,
    val openDt: String,
    val genres: List<Genre>,
    val audits: List<Audit>
)

data class Genre(
    val genreNm: String
)

data class Audit(
    val auditNo: String,
    val watchGradeNm: String
)
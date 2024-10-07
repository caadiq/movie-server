package com.beemer.movie.movie.dto

data class BoxOfficeMovieListDto(
    val movieListResult: MovieListResult?
)

data class MovieListResult(
    val movieList: List<Movie>
)

data class Movie(
    val movieCd: String,
    val movieNm: String,
    val movieNmEn: String,
    val openDt: String,
)
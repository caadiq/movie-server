package com.beemer.movie.movie.dto

data class BoxOfficeMovieListDto(
    val movieListResult: MovieListResult
)

data class MovieListResult(
    val totCnt: Int,
    val source: String,
    val movieList: List<Movie>
)

data class Movie(
    val movieCd: String,
    val movieNm: String,
    val movieNmEn: String,
    val prdtYear: String,
    val openDt: String,
    val typeNm: String,
    val prdtStatNm: String,
    val nationAlt: String,
    val genreAlt: String,
    val repNationNm: String,
    val repGenreNm: String,
    val directors: List<Director>,
    val companys: List<Company>
)

data class Director(
    val peopleNm: String
)

data class Company(
    val companyCd: String,
    val companyNm: String
)
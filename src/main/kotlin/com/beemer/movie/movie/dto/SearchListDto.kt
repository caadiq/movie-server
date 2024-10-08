package com.beemer.movie.movie.dto

import com.beemer.movie.common.dto.PageDto

data class SearchListDto(
    val page: PageDto,
    val movies: List<SearchList>
)

data class SearchList(
    val movieCode: String,
    val movieName: String,
    val posterUrl: String,
    val genre: String,
    val grade: String?,
    val openDate: String?
)
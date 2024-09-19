package com.beemer.movie.movie.service

import com.beemer.movie.movie.dto.BoxOfficeMovieListDto
import com.beemer.movie.movie.entity.Movies
import com.beemer.movie.movie.repository.MoviesRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.text.SimpleDateFormat
import java.time.LocalDate

@Service
class MoviesService(
    private val moviesRepository: MoviesRepository,
    private val webClient: WebClient
) {
    @Value("\${kobis.api.key}")
    private lateinit var kobisApiKey: String

    fun fetchMoviesFromApi() {
        val currentYear = LocalDate.now().year
        val url = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json?openStartDt=$currentYear&itemPerPage=30&key=$kobisApiKey"

        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(BoxOfficeMovieListDto::class.java)
            .subscribe { dto ->
                if (dto.movieListResult.movieList.isNotEmpty()) {
                    saveMovies(dto)
                }
            }
    }

    private fun saveMovies(dto: BoxOfficeMovieListDto) {
        val movies = dto.movieListResult.movieList.map { movie ->
            val openDate = SimpleDateFormat("yyyyMMdd").parse(movie.openDt)
            Movies(movie.movieCd, movie.movieNm, movie.movieNmEn, openDate)
        }
        moviesRepository.saveAll(movies)
    }
}
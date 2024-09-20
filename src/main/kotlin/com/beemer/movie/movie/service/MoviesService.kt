package com.beemer.movie.movie.service

import com.beemer.movie.common.exception.CustomException
import com.beemer.movie.common.exception.ErrorCode
import com.beemer.movie.movie.dto.BoxOfficeMovieListDto
import com.beemer.movie.movie.dto.KMDbMovieDetailsDto
import com.beemer.movie.movie.entity.MovieDetails
import com.beemer.movie.movie.entity.Movies
import com.beemer.movie.movie.repository.MoviesRepository
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Service
class MoviesService(
    private val moviesRepository: MoviesRepository,
    private val webClient: WebClient
) {
    @Value("\${kobis.api.key}")
    private lateinit var kobisApiKey: String

    @Value("\${kmdb.api.key}")
    private lateinit var kmdbApiKey: String

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

    fun getMovieDetails() {
        val today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val movies = moviesRepository.findByOpenDateAfter(today)

        movies.forEach { movie ->
            if (movie.details == null) {
                fetchMovieDetailsFromApi(movie.movieCode)
            }
        }
    }

    fun fetchMovieDetailsFromApi(movieCode: String) {
        val movie = moviesRepository.findById(movieCode)
            .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

        val movieName = movie.movieName
        val openDate = SimpleDateFormat("yyyyMMdd").format(movie.openDate)
        val url = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&detail=Y&query=$movieName&releaseDts=$openDate&ServiceKey=$kmdbApiKey"

        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String::class.java)
            .subscribe { responseBody ->
                val objectMapper = jacksonObjectMapper()
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

                val dto: KMDbMovieDetailsDto = objectMapper.readValue(responseBody)

                if (dto.Data.isNotEmpty()) {
                    saveMovieDetails(movieCode, dto)
                }
            }
    }

    private fun saveMovieDetails(movieCode: String, dto: KMDbMovieDetailsDto) {
        dto.Data[0].Result?.get(0)?.let { movie ->
            val movies = moviesRepository.findById(movieCode)
                .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

            val movieDetails = MovieDetails(
                movieCode,
                movie.runtime,
                movie.nation,
                movie.company,
                movie.prodYear,
                movie.plots.plot[0].plotText,
                movie.genre,
                movie.rating,
                movie.directors.director.joinToString(", ") { it.directorNm },
                movie.actors.actor.joinToString(", ") { it.actorNm },
                movie.posters,
                movie.keywords,
                movies
            )

            movies.details = movieDetails

            moviesRepository.save(movies)
        }
    }
}
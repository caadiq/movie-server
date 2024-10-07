package com.beemer.movie.movie.service

import com.beemer.movie.common.exception.CustomException
import com.beemer.movie.common.exception.ErrorCode
import com.beemer.movie.movie.dto.*
import com.beemer.movie.movie.entity.*
import com.beemer.movie.movie.repository.DailyBoxOfficeListRepository
import com.beemer.movie.movie.repository.MoviesRepository
import com.beemer.movie.movie.repository.WeeklyBoxOfficeListRepository
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@Service
class MoviesApiService(
    private val moviesRepository: MoviesRepository,
    private val dailyBoxOfficeListRepository: DailyBoxOfficeListRepository,
    private val weeklyBoxOfficeListRepository: WeeklyBoxOfficeListRepository,
    private val webClient: WebClient
) {
    @Value("\${kobis.api.key}")
    private lateinit var kobisApiKey: String

    @Value("\${kmdb.api.key}")
    private lateinit var kmdbApiKey: String

    private val logger = LoggerFactory.getLogger(MoviesApiService::class.java)

    private val objectMapper = jacksonObjectMapper().apply {
        configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Transactional
    fun fetchMoviesFromApi(page: Int) {
        val currentYear = LocalDate.now().year
        val url = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json?openStartDt=$currentYear&itemPerPage=100&curPage=$page&key=$kobisApiKey"

        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(BoxOfficeMovieListDto::class.java)
            .subscribe({ dto ->
                dto.movieListResult?.let {
                    if (it.movieList.isNotEmpty()) {
                        saveMovies(dto)
                    }
                }
//                fetchMoviesFromApi(page + 1)
            }, { error ->
                logger.error("[오류] fetchMoviesFromApi : ${error.message}")
//                fetchMoviesFromApi(page + 1)
            })
    }

    private fun saveMovies(dto: BoxOfficeMovieListDto) {
        val movies = dto.movieListResult?.movieList?.map { movie ->
            Movies(
                movieCode = movie.movieCd,
                movieName = movie.movieNm,
                movieNameEn = movie.movieNmEn
            )
        } ?: emptyList()

        if (movies.isNotEmpty()) {
            moviesRepository.saveAll(movies)
        }

        movies.forEach{ movie ->
            fetchMovieDetails1FromApi(movie.movieCode)
            fetchMovieDetails2FromApi(movie.movieCode)
        }
    }

    fun getMovieDetails() {
        val movies = moviesRepository.findAllByMovieCodeLike("2024%")

        movies.forEach {
            fetchMovieDetails1FromApi(it.movieCode)
            fetchMovieDetails2FromApi(it.movieCode)
        }
    }

    @Transactional
    fun fetchMovieDetails1FromApi(movieCode: String) {
        val movie = moviesRepository.findById(movieCode)
            .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

        if (movie.details1 == null) {
            val url = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json?movieCd=$movieCode&key=$kobisApiKey"

            webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(KobisMovieDetailsDto::class.java)
                .subscribe({ dto ->
                    saveMovieDetails1(dto)
                }, { error ->
                    logger.error("[오류] fetchMovieDetails1FromApi : ${error.message}")
                })
        }
    }

    private fun saveMovieDetails1(dto: KobisMovieDetailsDto) {
        dto.movieInfoResult?.let {
            it.movieInfo.let { movie ->
                val movies = moviesRepository.findById(movie.movieCd)
                    .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

                val movieDetails1 = MovieDetails1(
                    movieCode = movie.movieCd,
                    productYear = Year.of(movie.prdtYear.toInt()),
                    genre = if (movie.genres.isNotEmpty()) movie.genres.joinToString(",") { it.genreNm } else null,
                    openDate = if (movie.openDt.isNotEmpty()) SimpleDateFormat("yyyyMMdd").parse(movie.openDt) else null,
                    runtime = if (movie.showTm.isNotEmpty()) movie.showTm.toInt() else null,
                    grade = if (movie.audits.isNotEmpty() && movie.audits[0].watchGradeNm.isNotEmpty()) movie.audits[0].watchGradeNm else null,
                    movie = movies,
                )

                movies.details1 = movieDetails1
                moviesRepository.save(movies)
            }
        }
    }

    @Transactional
    fun fetchMovieDetails2FromApi(movieCode: String) {
        val movie = moviesRepository.findById(movieCode)
            .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

        val movieName = movie.movieName
        val movieNameEn = movie.movieNameEn

        val url = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&detail=Y&query=$movieName&ServiceKey=$kmdbApiKey"
        val urlEn = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&detail=Y&query=$movieNameEn&ServiceKey=$kmdbApiKey"

        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String::class.java)
            .subscribe({ responseBody ->
                try {
                    if (!responseBody.contains("인증키")) {
                        val dto: KMDbMovieDetailsDto = objectMapper.readValue(responseBody.replace("\\n", "\\\\n").replace("\\t", "\\\\t"))

                        if (dto.Data.isNotEmpty()) {
                            saveMovieDetails2(movieCode, dto)
                        } else {
                            webClient.get()
                                .uri(urlEn)
                                .retrieve()
                                .bodyToMono(String::class.java)
                                .subscribe({ responseBodyEn ->
                                    try {
                                        if (!responseBodyEn.contains("인증키가")) {
                                            val dtoEn: KMDbMovieDetailsDto = objectMapper.readValue(responseBodyEn.replace("\\n", "\\\\n").replace("\\t", "\\\\t"))

                                            if (dtoEn.Data.isNotEmpty()) {
                                                saveMovieDetails2(movieCode, dtoEn)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        logger.error("[오류] fetchMovieDetails2FromApi (EN) : ${e.message}")
                                        logger.error("Response Body (EN): $responseBodyEn")
                                    }
                                }, { error ->
                                    logger.error("[오류] fetchMovieDetails2FromApi (EN) : ${error.message}")
                                })
                        }
                    }
                } catch (e: Exception) {
                    logger.error("[오류] fetchMovieDetails2FromApi : ${e.message}")
                    logger.error("Response Body: $responseBody")
                }
            }, { error ->
                logger.error("[오류] fetchMovieDetails2FromApi : ${error.message}")
            })
    }

    private fun saveMovieDetails2(movieCode: String, dto: KMDbMovieDetailsDto) {
        dto.Data[0].Result?.get(0)?.let { movie ->
            val movies = moviesRepository.findById(movieCode)
                .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

            val movieDetails2 = MovieDetails2(
                movieCode,
                movie.nation,
                movie.company,
                movie.plots.plot[0].plotText,
                movie.genre,
                movie.rating,
                movie.posters,
                movie.keywords,
                movies
            )

            movies.details2 = movieDetails2
            moviesRepository.save(movies)
        }
    }

    @Transactional
    fun fetchDailyBoxOfficeListFromApi() {
        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val url = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?targetDt=$yesterday&key=$kobisApiKey"

        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(DailyBoxOfficeListDto::class.java)
            .subscribe ({ dto ->
                if (dto.boxOfficeResult.dailyBoxOfficeList.isNotEmpty()) {
                    saveDailyBoxOfficeList(dto)
                }
            }, { error ->
                logger.error("[오류] fetchDailyBoxOfficeListFromApi : ${error.message}")
            })
    }

    private fun saveDailyBoxOfficeList(dto: DailyBoxOfficeListDto) {
        val yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        dto.boxOfficeResult.dailyBoxOfficeList.forEach { dailyBoxOffice ->
            val movies = moviesRepository.findById(dailyBoxOffice.movieCd)
                .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

            val existingDailyBoxOfficeList = dailyBoxOfficeListRepository.findByMovieCodeAndDate(dailyBoxOffice.movieCd, yesterday)

            if (existingDailyBoxOfficeList == null) {
                val dailyBoxOfficeList = DailyBoxOfficeList(
                    movieCode = dailyBoxOffice.movieCd,
                    date = yesterday,
                    rank = dailyBoxOffice.rank.toInt(),
                    rankIncrement = dailyBoxOffice.rankInten,
                    audiCount = dailyBoxOffice.audiCnt,
                    audiIncrement = dailyBoxOffice.audiInten,
                    audiChange = dailyBoxOffice.audiChange,
                    audiAccumulate = dailyBoxOffice.audiAcc,
                    movie = movies
                )

                movies.dailyBoxOfficeLists.add(dailyBoxOfficeList)
                moviesRepository.save(movies)
            }
        }
    }

    @Transactional
    fun fetchWeeklyBoxOfficeListFromApi() {
        val lastSunday = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong()).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val url = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json?targetDt=$lastSunday&weekGb=0&key=$kobisApiKey"

        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(WeeklyBoxOfficeListDto::class.java)
            .subscribe { dto ->
                if (dto.boxOfficeResult.weeklyBoxOfficeList.isNotEmpty()) {
                    saveWeeklyBoxOfficeList(dto)
                }
            }
    }

    private fun saveWeeklyBoxOfficeList(dto: WeeklyBoxOfficeListDto) {
        val today = LocalDate.now()
        val lastSunday = Date.from(today.minusDays(today.dayOfWeek.value.toLong()).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val lastMonday = Date.from(today.minusDays(today.dayOfWeek.value.toLong() + 6).atStartOfDay(ZoneId.systemDefault()).toInstant())

        dto.boxOfficeResult.weeklyBoxOfficeList.forEach { weeklyBoxOffice ->
            val movies = moviesRepository.findById(weeklyBoxOffice.movieCd)
                .orElseThrow { throw CustomException(ErrorCode.MOVIE_NOT_FOUND) }

            val existingWeeklyBoxOfficeList = weeklyBoxOfficeListRepository.findByMovieCodeAndStartDateAndEndDate(weeklyBoxOffice.movieCd, lastMonday, lastSunday)

            if (existingWeeklyBoxOfficeList == null) {
                val weeklyBoxOfficeList = WeeklyBoxOfficeList(
                    movieCode = weeklyBoxOffice.movieCd,
                    startDate = lastMonday,
                    endDate = lastSunday,
                    rank = weeklyBoxOffice.rank.toInt(),
                    rankIncrement = weeklyBoxOffice.rankInten,
                    audiCount = weeklyBoxOffice.audiCnt,
                    audiIncrement = weeklyBoxOffice.audiInten,
                    audiChange = weeklyBoxOffice.audiChange,
                    audiAccumulate = weeklyBoxOffice.audiAcc,
                    movie = movies
                )

                movies.weeklyBoxOfficeLists.add(weeklyBoxOfficeList)
                moviesRepository.save(movies)
            }
        }
    }

    fun getDailyBoxOfficeDetails1() {
        val yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val movies = dailyBoxOfficeListRepository.findAllByDate(yesterday)

        movies.forEach {
            fetchMovieDetails1FromApi(it.movieCode)
        }
    }

    fun getDailyBoxOfficeDetails2() {
        val yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val movies = dailyBoxOfficeListRepository.findAllByDate(yesterday)

        movies.forEach {
            fetchMovieDetails2FromApi(it.movieCode)
        }
    }

    fun getWeeklyBoxOfficeDetails1() {
        val today = LocalDate.now()
        val lastSunday = Date.from(today.minusDays(today.dayOfWeek.value.toLong()).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val lastMonday = Date.from(today.minusDays(today.dayOfWeek.value.toLong() + 6).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val movies = weeklyBoxOfficeListRepository.findAllByStartDateAndEndDateOrderByRank(lastMonday, lastSunday)

        movies.forEach {
            fetchMovieDetails1FromApi(it.movieCode)
        }
    }

    fun getWeeklyBoxOfficeDetails2() {
        val today = LocalDate.now()
        val lastSunday = Date.from(today.minusDays(today.dayOfWeek.value.toLong()).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val lastMonday = Date.from(today.minusDays(today.dayOfWeek.value.toLong() + 6).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val movies = weeklyBoxOfficeListRepository.findAllByStartDateAndEndDateOrderByRank(lastMonday, lastSunday)

        movies.forEach {
            fetchMovieDetails2FromApi(it.movieCode)
        }
    }
}
package com.beemer.movie.movie.service

import com.beemer.movie.common.dto.PageDto
import com.beemer.movie.common.exception.CustomException
import com.beemer.movie.common.exception.ErrorCode
import com.beemer.movie.movie.dto.MovieDetailsDto
import com.beemer.movie.movie.dto.PosterBannerDto
import com.beemer.movie.movie.dto.RankList
import com.beemer.movie.movie.dto.RankListDto
import com.beemer.movie.movie.dto.ReleaseListDto
import com.beemer.movie.movie.dto.SearchList
import com.beemer.movie.movie.dto.SearchListDto
import com.beemer.movie.movie.repository.DailyBoxOfficeListRepository
import com.beemer.movie.movie.repository.MoviesRepository
import com.beemer.movie.movie.repository.WeeklyBoxOfficeListRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Service
class MoviesService(
    private val dailyBoxOfficeListRepository: DailyBoxOfficeListRepository,
    private val weeklyBoxOfficeListRepository: WeeklyBoxOfficeListRepository,
    private val moviesRepository: MoviesRepository,
    private val moviesApiService: MoviesApiService
) {
    fun getPosterBanner(): ResponseEntity<List<PosterBannerDto>> {
        val yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val dailyRankList = dailyBoxOfficeListRepository.findAllByDateOrderByRank(yesterday)

        val posterBannerDto = dailyRankList.map { dailyBoxOffice ->
            PosterBannerDto(
                movieCode = dailyBoxOffice.movieCode,
                posterUrl = dailyBoxOffice.movie.details2?.posterUrl?.split("|")?.firstOrNull() ?: ""
            )
        }.shuffled().take(5)

        return ResponseEntity.status(HttpStatus.OK).body(posterBannerDto)
    }

    fun getDailyRank(date: String): ResponseEntity<RankListDto> {
        val dateFormatted = Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val dailyRankList = dailyBoxOfficeListRepository.findAllByDateOrderByRank(dateFormatted)

        val prevDate = Date.from(LocalDate.parse(date).minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val nextDate = Date.from(LocalDate.parse(date).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val dateDto = com.beemer.movie.movie.dto.Date(
            prevDate = dailyBoxOfficeListRepository.findAllByDateOrderByRank(prevDate).firstOrNull()?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
            currenetDate = dateFormatted.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            nextDate = dailyBoxOfficeListRepository.findAllByDateOrderByRank(nextDate).firstOrNull()?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        )

        val dailyRankListDto = dailyRankList.map { dailyBoxOffice ->
            RankList(
                movieCode = dailyBoxOffice.movieCode,
                movieName = dailyBoxOffice.movie.movieName ?: "",
                posterUrl = dailyBoxOffice.movie.details2?.posterUrl?.split("|")?.firstOrNull() ?: "",
                genre = dailyBoxOffice.movie.details1?.genre ?: "",
                openDate = dailyBoxOffice.movie.details1?.openDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                rank = dailyBoxOffice.rank,
                rankIncrement = dailyBoxOffice.rankIncrement.toInt(),
                audiCount = dailyBoxOffice.audiCount.toInt(),
                audiIncrement = dailyBoxOffice.audiIncrement.toInt(),
                audiAccumulate = dailyBoxOffice.audiAccumulate.toInt()
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(RankListDto(dateDto, dailyRankListDto))
    }

    fun getWeeklyRank(startDate: String, endDate: String): ResponseEntity<RankListDto> {
        val startDateFormatted = Date.from(LocalDate.parse(startDate).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDateFormatted = Date.from(LocalDate.parse(endDate).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val weeklyRankList = weeklyBoxOfficeListRepository.findAllByStartDateAndEndDateOrderByRank(startDateFormatted, endDateFormatted)

        val prevStartDate = Date.from(LocalDate.parse(startDate).minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val prevEndDate = Date.from(LocalDate.parse(endDate).minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val nextStartDate = Date.from(LocalDate.parse(startDate).plusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val nextEndDate = Date.from(LocalDate.parse(endDate).plusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val dateDto = com.beemer.movie.movie.dto.Date(
            prevDate = weeklyBoxOfficeListRepository.findAllByStartDateAndEndDateOrderByRank(prevStartDate, prevEndDate).firstOrNull()?.startDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
            currenetDate = startDateFormatted.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            nextDate = weeklyBoxOfficeListRepository.findAllByStartDateAndEndDateOrderByRank(nextStartDate, nextEndDate).firstOrNull()?.startDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        )

        val weeklyRankListDto = weeklyRankList.map { weeklyBoxOffice ->
            RankList(
                movieCode = weeklyBoxOffice.movieCode,
                movieName = weeklyBoxOffice.movie.movieName ?: "",
                genre = weeklyBoxOffice.movie.details1?.genre ?: "",
                posterUrl = weeklyBoxOffice.movie.details2?.posterUrl?.split("|")?.firstOrNull() ?: "",
                openDate = weeklyBoxOffice.movie.details1?.openDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                rank = weeklyBoxOffice.rank,
                rankIncrement = weeklyBoxOffice.rankIncrement.toInt(),
                audiCount = weeklyBoxOffice.audiCount.toInt(),
                audiIncrement = weeklyBoxOffice.audiIncrement.toInt(),
                audiAccumulate = weeklyBoxOffice.audiAccumulate.toInt()
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(RankListDto(dateDto, weeklyRankListDto))
    }

    fun getRecentRelease(limit: Int) : ResponseEntity<List<ReleaseListDto>> {
        val startDate = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDate = Date()

        val movies = moviesRepository.findByOpenDateBetweenOrderByOpenDateDesc(startDate, endDate)

        movies.forEach {
            if (it.details1 == null) {
                moviesApiService.fetchMovieDetails1FromApi(it.movieCode)
            }

            if (it.details2 == null) {
                moviesApiService.fetchMovieDetails2FromApi(it.movieCode)
            }
        }

        val releaseListDto = movies.map { movie ->
            ReleaseListDto(
                movieCode = movie.movieCode,
                movieName = movie.movieName ?: "",
                posterUrl = movie.details2?.posterUrl?.split("|")?.firstOrNull() ?: "",
                releaseDate = movie.details1?.openDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(releaseListDto.take(limit))
    }

    fun getComingRelease(limit: Int) : ResponseEntity<List<ReleaseListDto>> {
        val startDate = Date()
        val movies = moviesRepository.findByOpenDateAfterOrderByOpenDate(startDate)

        movies.forEach {
            if (it.details1 == null) {
                moviesApiService.fetchMovieDetails1FromApi(it.movieCode)
            }

            if (it.details2 == null) {
                moviesApiService.fetchMovieDetails2FromApi(it.movieCode)
            }
        }

        val releaseListDto = movies.map { movie ->
            ReleaseListDto(
                movieCode = movie.movieCode,
                movieName = movie.movieName ?: "",
                posterUrl = movie.details2?.posterUrl?.split("|")?.firstOrNull() ?: "",
                releaseDate = movie.details1?.openDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(releaseListDto.take(limit))
    }

    fun getMovieList(page: Int, limit: Int, query: String) : ResponseEntity<SearchListDto> {
        val limitAdjusted = 1.coerceAtLeast(50.coerceAtMost(limit))
        val pageable = PageRequest.of(page, limitAdjusted)

        val movies = moviesRepository.findAllByMovieNameOrMovieNameEnOrGenreOrKeywords(pageable, query.replace(" ", ""))

        if (movies.content.isEmpty() && movies.totalElements > 0) {
            throw CustomException(ErrorCode.MOVIE_NOT_FOUND)
        }

        movies.forEach {
            if (it.details1 == null) {
                moviesApiService.fetchMovieDetails1FromApi(it.movieCode)
            }

            if (it.details2 == null) {
                moviesApiService.fetchMovieDetails2FromApi(it.movieCode)
            }
        }

        val prevPage = if (movies.hasPrevious()) movies.number - 1 else null
        val currentPage = movies.number
        val nextPage = if (movies.hasNext()) movies.number + 1 else null

        val pages = PageDto(prevPage, currentPage, nextPage)

        val movieList = movies.content.map {
            SearchList(
                movieCode = it.movieCode,
                movieName = it.movieName ?: "",
                posterUrl = it.details2?.posterUrl?.split("|")?.firstOrNull() ?: "",
                genre = it.details1?.genre ?: it.details2?.genres ?: "",
                grade = it.details1?.grade ?: it.details2?.rating,
                openDate = it.details1?.openDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()?.toString()
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(SearchListDto(pages, movieList))
    }

    fun getMovieDetails(movieCode: String) : ResponseEntity<MovieDetailsDto> {
        val movie = moviesRepository.findById(movieCode)
            .orElseThrow { CustomException(ErrorCode.MOVIE_NOT_FOUND) }

        if (movie.details1 == null) {
            moviesApiService.fetchMovieDetails1FromApi(movieCode)
        }

        if (movie.details2 == null) {
            moviesApiService.fetchMovieDetails2FromApi(movieCode)
        }

        val movieDetailsDto = MovieDetailsDto(
            movieCode = movie.movieCode,
            movieName = movie.movieName ?: "",
            movieNameEn = movie.movieNameEn,
            openDate = movie.details1?.openDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
            posterUrl = movie.details2?.posterUrl?.split("|")?.firstOrNull() ?: "",
            genres = movie.details1?.genre?.split(",") ?: movie.details2?.genres?.split(",") ?: emptyList(),
            runTime = movie.details1?.runtime,
            nation = movie.details2?.nation,
            grade = movie.details1?.grade ?: movie.details2?.rating,
            plot = movie.details2?.plot
        )

        return ResponseEntity.status(HttpStatus.OK).body(movieDetailsDto)
    }
}
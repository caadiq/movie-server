package com.beemer.movie.movie.service

import com.beemer.movie.movie.dto.RankListDto
import com.beemer.movie.movie.dto.ReleaseListDto
import com.beemer.movie.movie.repository.DailyBoxOfficeListRepository
import com.beemer.movie.movie.repository.MoviesRepository
import com.beemer.movie.movie.repository.WeeklyBoxOfficeListRepository
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
    private val moviesRepository: MoviesRepository
) {
    fun getDailyRank(date: String): ResponseEntity<List<RankListDto>> {
        val dateFormatted = Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val dailyRankList = dailyBoxOfficeListRepository.getAllByDateOrderByRank(dateFormatted)

        val dailyRankListDto = dailyRankList.map { dailyBoxOffice ->
            RankListDto(
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

        return ResponseEntity.status(HttpStatus.OK).body(dailyRankListDto)
    }

    fun getWeeklyRank(startDate: String, endDate: String): ResponseEntity<List<RankListDto>> {
        val startDateFormatted = Date.from(LocalDate.parse(startDate).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDateFormatted = Date.from(LocalDate.parse(endDate).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val weeklyRankList = weeklyBoxOfficeListRepository.findAllByStartDateAndEndDateOrderByRank(startDateFormatted, endDateFormatted)

        val weeklyRankListDto = weeklyRankList.map { weeklyBoxOffice ->
            RankListDto(
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

        return ResponseEntity.status(HttpStatus.OK).body(weeklyRankListDto)
    }

    fun getRecentRelease(limit: Int) : ResponseEntity<List<ReleaseListDto>> {
        val startDate = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDate = Date()

        val movies = moviesRepository.findByOpenDateBetweenOrderByOpenDateDesc(startDate, endDate)

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
}
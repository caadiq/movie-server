package com.beemer.movie.movie.service

import com.beemer.movie.movie.dto.DailyRankListDto
import com.beemer.movie.movie.dto.WeeklyRankListDto
import com.beemer.movie.movie.repository.DailyBoxOfficeListRepository
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
    private val weeklyBoxOfficeListRepository: WeeklyBoxOfficeListRepository
) {
    fun getDailyRank(date: String) : ResponseEntity<List<DailyRankListDto>> {
        val dateFormatted = Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val dailyRankList = dailyBoxOfficeListRepository.getAllByDateOrderByRank(dateFormatted)

        val dailyRankListDto = dailyRankList.map { dailyBoxOffice ->
            DailyRankListDto(
                movieCode = dailyBoxOffice.movieCode,
                movieName = dailyBoxOffice.movie.movieName ?: "",
                posterUrl = dailyBoxOffice.movie.details?.posterUrl?.split("|")?.firstOrNull() ?: "",
                date = dailyBoxOffice.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                rank = dailyBoxOffice.rank.toInt(),
                rankIncrement = dailyBoxOffice.rankIncrement.toInt(),
                audiCount = dailyBoxOffice.audiCount.toInt(),
                audiIncrement = dailyBoxOffice.audiIncrement.toInt(),
                audiAccumulate = dailyBoxOffice.audiAccumulate.toInt()
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(dailyRankListDto)
    }

    fun getWeeklyRank(startDate: String, endDate: String) : ResponseEntity<List<WeeklyRankListDto>> {
        val startDateFormatted = Date.from(LocalDate.parse(startDate).atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDateFormatted = Date.from(LocalDate.parse(endDate).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val weeklyRankList = weeklyBoxOfficeListRepository.getAllByStartDateAndEndDateOrderByRank(startDateFormatted, endDateFormatted)

        val weeklyRankListDto = weeklyRankList.map { weeklyBoxOffice ->
            WeeklyRankListDto(
                movieCode = weeklyBoxOffice.movieCode,
                movieName = weeklyBoxOffice.movie.movieName ?: "",
                posterUrl = weeklyBoxOffice.movie.details?.posterUrl?.split("|")?.firstOrNull() ?: "",
                startDate = weeklyBoxOffice.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                endDate = weeklyBoxOffice.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                rank = weeklyBoxOffice.rank.toInt(),
                rankIncrement = weeklyBoxOffice.rankIncrement.toInt(),
                audiCount = weeklyBoxOffice.audiCount.toInt(),
                audiIncrement = weeklyBoxOffice.audiIncrement.toInt(),
                audiAccumulate = weeklyBoxOffice.audiAccumulate.toInt()
            )
        }

        return ResponseEntity.status(HttpStatus.OK).body(weeklyRankListDto)
    }
}
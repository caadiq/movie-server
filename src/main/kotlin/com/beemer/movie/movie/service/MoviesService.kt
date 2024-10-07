package com.beemer.movie.movie.service

import com.beemer.movie.movie.dto.PosterBannerDto
import com.beemer.movie.movie.dto.RankList
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
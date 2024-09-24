package com.beemer.movie.movie.controller

import com.beemer.movie.movie.dto.DailyRankListDto
import com.beemer.movie.movie.dto.ReleaseListDto
import com.beemer.movie.movie.dto.WeeklyRankListDto
import com.beemer.movie.movie.service.MoviesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/movie")
class MoviesController(private val moviesService: MoviesService) {

    @GetMapping("/rank/daily")
    fun getDailyRank(
        @RequestParam date: String
    ) : ResponseEntity<List<DailyRankListDto>> {
        return moviesService.getDailyRank(date)
    }

    @GetMapping("/rank/weekly")
    fun getWeeklyRank(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ) : ResponseEntity<List<WeeklyRankListDto>> {
        return moviesService.getWeeklyRank(startDate, endDate)
    }

    @GetMapping("/release/latest")
    fun getLatestRelease(
        @RequestParam limit: Int
    ) : ResponseEntity<List<ReleaseListDto>> {
        return moviesService.getLatestRelease(limit)
    }

    @GetMapping("/release/coming")
    fun getComingRelease(
        @RequestParam limit: Int
    ) : ResponseEntity<List<ReleaseListDto>> {
        return moviesService.getComingRelease(limit)
    }
}
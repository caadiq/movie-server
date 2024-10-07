package com.beemer.movie.movie.controller

import com.beemer.movie.movie.dto.PosterBannerDto
import com.beemer.movie.movie.dto.RankListDto
import com.beemer.movie.movie.dto.ReleaseListDto
import com.beemer.movie.movie.service.MoviesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/movie")
class MoviesController(private val moviesService: MoviesService) {

    @GetMapping("/poster/banner")
    fun getPosterBanner() : ResponseEntity<List<PosterBannerDto>> {
        return moviesService.getPosterBanner()
    }

    @GetMapping("/rank/daily")
    fun getDailyRank(
        @RequestParam date: String
    ) : ResponseEntity<RankListDto> {
        return moviesService.getDailyRank(date)
    }

    @GetMapping("/rank/weekly")
    fun getWeeklyRank(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ) : ResponseEntity<RankListDto> {
        return moviesService.getWeeklyRank(startDate, endDate)
    }

    @GetMapping("/release/recent")
    fun getRecentRelease(
        @RequestParam limit: Int
    ) : ResponseEntity<List<ReleaseListDto>> {
        return moviesService.getRecentRelease(limit)
    }

    @GetMapping("/release/coming")
    fun getComingRelease(
        @RequestParam limit: Int
    ) : ResponseEntity<List<ReleaseListDto>> {
        return moviesService.getComingRelease(limit)
    }
}
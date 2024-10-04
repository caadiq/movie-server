package com.beemer.movie.movie.controller

import com.beemer.movie.movie.dto.RankListDto
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
    ) : ResponseEntity<List<RankListDto>> {
        return moviesService.getDailyRank(date)
    }

    @GetMapping("/rank/weekly")
    fun getWeeklyRank(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ) : ResponseEntity<List<RankListDto>> {
        return moviesService.getWeeklyRank(startDate, endDate)
    }

//    @GetMapping("/release/recent")
//    fun getRecentRelease(
//        @RequestParam limit: Int
//    ) : ResponseEntity<List<ReleaseListDto>> {
//        return moviesService.getLatestRelease(limit)
//    }
//
//    @GetMapping("/release/coming")
//    fun getComingRelease(
//        @RequestParam limit: Int
//    ) : ResponseEntity<List<ReleaseListDto>> {
//        return moviesService.getComingRelease(limit)
//    }
}
package com.beemer.movie.movie.scheduler

import com.beemer.movie.movie.service.MoviesService
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MoviesScheduler(private val moviesService: MoviesService) {

    @PostConstruct
    fun init() {
//        moviesService.getMoviesFromApi()
//        moviesService.fetchMovieDetailsFromApi()
        moviesService.fetchDailyBoxOfficeListFromApi()
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun fetchMovies() {
        moviesService.fetchMoviesFromApi()
    }

    @Scheduled(cron = "0 2 0 * * *")
    fun fetchMoviesDetails() {
        moviesService.fetchMovieDetailsFromApi()
    }

    @Scheduled(cron = "0 5 0 * * *")
    fun fetchDailyBoxOfficeList() {
        moviesService.fetchDailyBoxOfficeListFromApi()
    }
}
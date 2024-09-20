package com.beemer.movie.movie.scheduler

import com.beemer.movie.movie.service.MoviesApiService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MoviesApiScheduler(private val moviesApiService: MoviesApiService) {

    @Scheduled(cron = "0 0 0 * * *")
    fun fetchMovies() {
        moviesApiService.fetchMoviesFromApi()
    }

    @Scheduled(cron = "0 2 0 * * *")
    fun fetchMoviesDetails() {
        moviesApiService.fetchMovieDetailsFromApi()
    }

    @Scheduled(cron = "0 5 0 * * *")
    fun fetchDailyBoxOfficeList() {
        moviesApiService.fetchDailyBoxOfficeListFromApi()
    }

    @Scheduled(cron = "0 5 0 * * MON")
    fun fetchWeeklyBoxOfficeList() {
        moviesApiService.fetchWeeklyBoxOfficeListFromApi()
    }
}
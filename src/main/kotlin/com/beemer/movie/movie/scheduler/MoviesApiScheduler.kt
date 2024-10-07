package com.beemer.movie.movie.scheduler

import com.beemer.movie.movie.service.MoviesApiService
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MoviesApiScheduler(private val moviesApiService: MoviesApiService) {

    @PostConstruct
    fun init() {
//        moviesApiService.getMovieDetails()
//        fetchMovies()
//        fetchBoxOfficeList()
//        fetchBoxOfficeDetails1()
//        fetchBoxOfficeDetails2()
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun fetchMovies() {
        moviesApiService.fetchMoviesFromApi(1)
    }

    @Scheduled(cron = "0 3 0 * * *")
    fun fetchBoxOfficeList() {
        moviesApiService.fetchDailyBoxOfficeListFromApi()
        moviesApiService.fetchWeeklyBoxOfficeListFromApi()
    }

    @Scheduled(cron = "0 5 0 * * *")
    fun fetchBoxOfficeDetails1() {
        moviesApiService.getDailyBoxOfficeDetails1()
        moviesApiService.getWeeklyBoxOfficeDetails1()
    }

    @Scheduled(cron = "30 5 0 * * *")
    fun fetchBoxOfficeDetails2() {
        moviesApiService.getDailyBoxOfficeDetails2()
        moviesApiService.getWeeklyBoxOfficeDetails2()
    }
}
package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.Movies
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MoviesRepository : JpaRepository<Movies, String> {
    fun findByOpenDateAfter(openDate: Date): List<Movies>
    fun findByOpenDateBetweenOrderByOpenDateDesc(startDate: Date, endDate: Date): List<Movies>
    fun findByOpenDateAfterOrderByOpenDate(openDate: Date): List<Movies>
}
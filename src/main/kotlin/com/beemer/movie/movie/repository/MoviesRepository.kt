package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.Movies
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface MoviesRepository : JpaRepository<Movies, String> {
    override fun findAll(pageable: Pageable): Page<Movies>

    @Query("SELECT m FROM Movies m JOIN m.details1 d WHERE d.openDate BETWEEN :startDate AND :endDate ORDER BY d.openDate DESC")
    fun findByOpenDateBetweenOrderByOpenDateDesc(startDate: Date, endDate: Date): List<Movies>

    @Query("SELECT m FROM Movies m JOIN m.details1 d WHERE d.openDate > :openDate ORDER BY d.openDate")
    fun findByOpenDateAfterOrderByOpenDate(openDate: Date): List<Movies>

    fun findAllByMovieCodeLike(n: String) : List<Movies>
}
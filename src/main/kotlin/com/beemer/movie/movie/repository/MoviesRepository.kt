package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.Movies
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MoviesRepository : JpaRepository<Movies, String> {
    @Query("SELECT m FROM Movies m JOIN m.details1 d WHERE d.openDate BETWEEN :startDate AND :endDate ORDER BY d.openDate DESC")
    fun findByOpenDateBetweenOrderByOpenDateDesc(startDate: Date, endDate: Date): List<Movies>

    @Query("SELECT m FROM Movies m JOIN m.details1 d WHERE d.openDate > :openDate ORDER BY d.openDate")
    fun findByOpenDateAfterOrderByOpenDate(openDate: Date): List<Movies>

    fun findAllByMovieCodeLike(n: String) : List<Movies>

    @Query("SELECT DISTINCT m FROM Movies m LEFT JOIN m.details1 d1 LEFT JOIN m.details2 d2 " +
            "WHERE (LOWER(m.movieName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(m.movieNameEn) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d1.genre) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d2.genres) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d2.keywords) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND NOT ((d1.genre LIKE '%성인물(에로)%' OR d1.genre LIKE '%에로%') " +
            "AND (d1.grade = '연소자관람불가' OR d1.grade = '청소년관람불가'))")
    fun findAllByMovieNameOrMovieNameEnOrGenreOrKeywords(pageable: Pageable, query: String) : Page<Movies>
}
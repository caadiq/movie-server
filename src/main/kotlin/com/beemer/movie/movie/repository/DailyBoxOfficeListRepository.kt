package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.DailyBoxOfficeList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DailyBoxOfficeListRepository : JpaRepository<DailyBoxOfficeList, String> {
    fun findByMovieCodeAndDate(movieCode: String, date: Date): DailyBoxOfficeList?

    @Query("SELECT d FROM DailyBoxOfficeList d WHERE d.date = :date ORDER BY CAST(d.rank AS int)")
    fun getAllByDateOrderByRank(@Param("date") date: Date): List<DailyBoxOfficeList>

    fun findAllByDate(date: Date): List<DailyBoxOfficeList>
}
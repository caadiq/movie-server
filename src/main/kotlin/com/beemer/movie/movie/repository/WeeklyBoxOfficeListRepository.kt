package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.WeeklyBoxOfficeList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WeeklyBoxOfficeListRepository : JpaRepository<WeeklyBoxOfficeList, String> {
    fun findByMovieCodeAndStartDateAndEndDate(movieCode: String, startDate: Date, endDate: Date): WeeklyBoxOfficeList?
    fun getAllByStartDateAndEndDateOrderByRank(startDate: Date, endDate: Date): List<WeeklyBoxOfficeList>
}
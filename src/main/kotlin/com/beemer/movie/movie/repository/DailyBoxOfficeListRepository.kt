package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.DailyBoxOfficeList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyBoxOfficeListRepository : JpaRepository<DailyBoxOfficeList, String>
package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.Movies
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MoviesRepository : JpaRepository<Movies, String> {
    override fun findAll(pageable: Pageable): Page<Movies>
}
package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.MovieDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieDetailsRepository : JpaRepository<MovieDetails, String>
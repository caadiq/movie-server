package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.MovieDetails2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieDetailsRepository : JpaRepository<MovieDetails2, String>
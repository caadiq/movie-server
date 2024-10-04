package com.beemer.movie.movie.repository

import com.beemer.movie.movie.entity.MovieDetails1
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieDetails1Repository : JpaRepository<MovieDetails1, String>
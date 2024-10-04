package com.beemer.movie.movie.entity

import jakarta.persistence.*
import java.time.Year
import java.util.Date

@Entity
@Table(name = "MovieDetails1")
data class MovieDetails1(
    @Id
    @Column(name = "movie_code", nullable = false)
    val movieCode: String,

    @Column(name = "product_year")
    val productYear: Year?,

    @Column(name = "genre")
    val genre: String?,

    @Column(name = "open_date")
    val openDate: Date?,

    @Column(name = "runtime")
    val runtime: Int?,

    @Column(name = "grade")
    val grade: String?,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_code")
    @MapsId
    val movie: Movies
)
package com.beemer.movie.movie.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "Movies")
data class Movies(
    @Id
    @Column(name = "movie_code")
    val movieCode: String,

    @Column(name = "movie_name", nullable = true)
    val movieName: String?,

    @Column(name = "movie_name_en", nullable = true)
    val movieNameEn: String?,

    @Column(name = "open_date", nullable = true)
    val openDate: Date?
)

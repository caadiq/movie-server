package com.beemer.movie.movie.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "Movies")
data class Movies(
    @Id
    @Column(name = "movie_code", nullable = false)
    val movieCode: String,

    @Column(name = "movie_name")
    val movieName: String?,

    @Column(name = "movie_name_en")
    val movieNameEn: String?,

    @Column(name = "open_date")
    val openDate: Date?,

    @OneToOne(mappedBy = "movie", cascade = [CascadeType.ALL])
    var details: MovieDetails? = null
)

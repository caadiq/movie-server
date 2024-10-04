package com.beemer.movie.movie.entity

import jakarta.persistence.*

@Entity
@Table(name = "MovieDetails2")
data class MovieDetails2(
    @Id
    @Column(name = "movie_code", nullable = false)
    val movieCode: String,

    @Column(name = "nation")
    val nation: String?,

    @Column(name = "company")
    val company: String?,

    @Column(name = "plot")
    val plot: String?,

    @Column(name = "genres")
    val genres: String?,

    @Column(name = "rating")
    val rating: String?,

    @Column(name = "poster_url")
    val posterUrl: String?,

    @Column(name = "keywords")
    val keywords: String?,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_code")
    @MapsId
    val movie: Movies
)
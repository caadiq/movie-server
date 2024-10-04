package com.beemer.movie.movie.entity

import jakarta.persistence.*

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

    @OneToOne(mappedBy = "movie", cascade = [CascadeType.ALL])
    var details1: MovieDetails1? = null,

    @OneToOne(mappedBy = "movie", cascade = [CascadeType.ALL])
    var details2: MovieDetails2? = null,

    @OneToMany(mappedBy = "movie", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var dailyBoxOfficeLists: MutableList<DailyBoxOfficeList> = mutableListOf(),

    @OneToMany(mappedBy = "movie", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var weeklyBoxOfficeLists: MutableList<WeeklyBoxOfficeList> = mutableListOf()
)

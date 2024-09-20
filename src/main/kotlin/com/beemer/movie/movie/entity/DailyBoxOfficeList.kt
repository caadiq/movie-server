package com.beemer.movie.movie.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "DailyBoxOfficeList")
data class DailyBoxOfficeList(
    @Id
    @Column(name = "movie_code", nullable = false)
    val movieCode: String,

    @Column(name = "rank")
    val rank: String,

    @Column(name = "rank_increment")
    val rankIncrement: String,

    @Column(name = "audi_count")
    val audiCount: String,

    @Column(name = "audi_increment")
    val audiIncrement: String,

    @Column(name = "audi_change")
    val audiChange: String,

    @Column(name = "audi_accumulate")
    val audiAccumulate: String,

    @Column(name = "date")
    val date: Date,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_code")
    @MapsId
    val movie: Movies
)

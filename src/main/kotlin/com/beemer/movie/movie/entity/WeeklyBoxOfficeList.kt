package com.beemer.movie.movie.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "WeeklyBoxOfficeList")
data class WeeklyBoxOfficeList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

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

    @Column(name = "start_date")
    val startDate: Date,

    @Column(name = "end_date")
    val endDate: Date,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_code", insertable = false, updatable = false)
    val movie: Movies
)

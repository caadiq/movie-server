package com.beemer.movie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class MovieApplication

fun main(args: Array<String>) {
    runApplication<MovieApplication>(*args)
}

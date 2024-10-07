package com.beemer.movie.common.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        val connectionProvider = ConnectionProvider.builder("webClient")
            .maxConnections(200000) // 최대 연결 수 설정
            .pendingAcquireMaxCount(200000) // 대기 중인 최대 요청 수 설정
            .pendingAcquireTimeout(Duration.ofSeconds(60)) // 대기 중인 요청의 타임아웃 설정
            .build()

        val httpClient = HttpClient.create(connectionProvider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 연결 타임아웃
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(60))
            }
            .responseTimeout(Duration.ofSeconds(60)) // 응답 타임아웃

        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) }
            .build()

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(exchangeStrategies)
            .build()
    }
}
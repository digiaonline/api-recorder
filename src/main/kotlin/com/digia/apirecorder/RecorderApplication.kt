package com.digia.apirecorder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


fun main(args: Array<String>) {
    runApplication<RecorderApplication>(*args)
}

@SpringBootApplication
@EnableJpaRepositories
class RecorderApplication
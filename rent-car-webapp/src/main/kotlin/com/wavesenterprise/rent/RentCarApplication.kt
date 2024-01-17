package com.wavesenterprise.rent

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class RentCarApplication

fun main(args: Array<String>) {
    SpringApplication.run(RentCarApplication::class.java, *args)
}

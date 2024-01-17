package com.wavesenterprise.rent.config

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.wavesenterprise.api.BlackListContract
import com.wavesenterprise.api.RentCarContract
import com.wavesenterprise.impl.BlackListContractImpl
import com.wavesenterprise.impl.RentCarContractImpl
import com.wavesenterprise.sdk.node.client.blocking.credentials.SenderAddressProvider
import com.wavesenterprise.sdk.spring.autoconfigure.contract.annotation.Contract
import com.wavesenterprise.sdk.spring.autoconfigure.contract.annotation.EnableContracts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableContracts(
    contracts = [
        Contract(
            api = RentCarContract::class,
            impl = RentCarContractImpl::class,
            name = "rentCarContract",
        ),
        Contract(
            api = BlackListContract::class,
            impl = BlackListContractImpl::class,
            name = "blackListContract"
        ),
    ]
)
class RentCarAppConfiguration {

    @Bean
    fun nodeAddressProvider(): SenderAddressProvider = HttpSenderAddressProvider()

    @Bean
    fun kotlinModule() = KotlinModule.Builder().build()

    @Bean
    fun javaTimeModule() = JavaTimeModule()
}

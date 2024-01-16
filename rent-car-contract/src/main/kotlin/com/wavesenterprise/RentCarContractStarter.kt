package com.wavesenterprise

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wavesenterprise.impl.RentCarContractImpl
import com.wavesenterprise.sdk.contract.grpc.GrpcJacksonContractDispatcherBuilder

fun main() {
    val dispatcher = GrpcJacksonContractDispatcherBuilder
        .builder()
        .contractHandlerType(RentCarContractImpl::class.java)
        .objectMapper(jacksonObjectMapper())
        .build()
    dispatcher.dispatch()
}

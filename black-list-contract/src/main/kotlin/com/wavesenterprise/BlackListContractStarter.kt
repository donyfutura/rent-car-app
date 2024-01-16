package com.wavesenterprise

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wavesenterprise.impl.BlackListContractImpl
import com.wavesenterprise.sdk.contract.grpc.GrpcJacksonContractDispatcherBuilder

fun main() {
    val dispatcher = GrpcJacksonContractDispatcherBuilder
        .builder()
        .contractHandlerType(BlackListContractImpl::class.java)
        .objectMapper(jacksonObjectMapper())
        .build()
    dispatcher.dispatch()
}

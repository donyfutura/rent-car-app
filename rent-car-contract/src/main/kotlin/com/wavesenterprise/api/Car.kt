package com.wavesenterprise.api

data class Car(
    val number: Int,
    val name: String,
    var renter: String? = null,
    var date: Long? = null,
)

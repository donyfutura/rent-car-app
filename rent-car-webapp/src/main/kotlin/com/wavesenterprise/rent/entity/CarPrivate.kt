package com.wavesenterprise.rent.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class CarPrivate(

    @Id
    val number: Int,
    val renterPassportNumber: String,
    val txId: String,
    val policyId: String,
)

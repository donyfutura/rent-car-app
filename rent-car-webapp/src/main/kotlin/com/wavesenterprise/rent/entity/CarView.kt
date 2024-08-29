package com.wavesenterprise.rent.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class CarView (

    @Id
    val number: Int,
    val renter: String?,
    val name: String,
    val renterPassportNumber: String,
    val txId: String,
    val policyId: String,
)

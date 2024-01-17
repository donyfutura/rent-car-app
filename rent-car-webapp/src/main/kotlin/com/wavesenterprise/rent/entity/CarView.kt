package com.wavesenterprise.rent.entity

import javax.persistence.Entity
import javax.persistence.Id

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

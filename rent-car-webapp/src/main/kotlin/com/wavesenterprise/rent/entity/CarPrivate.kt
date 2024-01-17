package com.wavesenterprise.rent.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class CarPrivate(

    @Id
    val number: Int,
    val renterPassportNumber: String,
    val txId: String,
    val policyId: String,
)

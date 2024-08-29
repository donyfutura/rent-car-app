package com.wavesenterprise.rent.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class CarPublic(

    @Id
    val number: Int,
    val renter: String?,
    val name: String,
)

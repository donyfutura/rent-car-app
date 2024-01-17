package com.wavesenterprise.rent.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class CarPublic(

    @Id
    val number: Int,
    val renter: String?,
    val name: String,
)

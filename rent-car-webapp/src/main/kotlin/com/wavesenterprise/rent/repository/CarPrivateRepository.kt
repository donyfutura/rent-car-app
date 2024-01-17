package com.wavesenterprise.rent.repository

import com.wavesenterprise.rent.entity.CarPrivate
import com.wavesenterprise.rent.entity.CarPublic
import org.springframework.data.jpa.repository.JpaRepository

interface CarPrivateRepository : JpaRepository<CarPrivate, String>

package com.wavesenterprise.rent.repository

import com.wavesenterprise.rent.entity.CarPublic
import org.springframework.data.jpa.repository.JpaRepository

interface CarPublicRepository : JpaRepository<CarPublic, String>

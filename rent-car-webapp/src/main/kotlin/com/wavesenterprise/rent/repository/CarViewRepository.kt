package com.wavesenterprise.rent.repository

import com.wavesenterprise.rent.entity.CarView
import org.springframework.data.jpa.repository.JpaRepository

interface CarViewRepository : JpaRepository<CarView, String>

package com.wavesenterprise.rent.controller

import com.wavesenterprise.api.Car
import com.wavesenterprise.rent.entity.CarPublic
import com.wavesenterprise.rent.entity.CarView
import com.wavesenterprise.rent.service.RentCarService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rent")
class RentCarController(
    private val rentCarService: RentCarService,
) {

    @PostMapping("/init-contract")
    @Parameter(
        `in` = ParameterIn.HEADER,
        name = "X-Tx-Sender",
        required = true,
        example = "3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM"
    )
    fun init(): String {
        return rentCarService.initRent()
    }

    @GetMapping("/car/rented")
    fun getRentedCars(): List<CarView> {
        return rentCarService.getRented()
    }

    @GetMapping("/car/free")
    fun getFreeCars(): List<CarPublic> {
        return rentCarService.getFree()
    }

    @PostMapping("/car/create")
    @Parameter(
        `in` = ParameterIn.HEADER,
        name = "X-Tx-Sender",
        required = true,
    )
    fun createCar(@RequestBody car: Car): String {
        return rentCarService.createCar(car)
    }

    @PostMapping("/car/{carNumber}/{renterPassportNumber}/rent")
    @Parameter(
        `in` = ParameterIn.HEADER,
        name = "X-Tx-Sender",
        required = true,
    )
    fun rentCar(
        @PathVariable carNumber: Int,
        @PathVariable renterPassportNumber: String
    ): String {
        return rentCarService.rentCar(carNumber, renterPassportNumber)
    }
}

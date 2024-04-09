package com.wavesenterprise.api

import com.wavesenterprise.sdk.contract.api.annotation.ContractAction
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit

interface RentCarContract {

    @ContractInit
    fun create()

    @ContractAction
    fun rentCar(carNumber: Int)

    @ContractAction
    fun changeCarRenter(carNumber: Int, renter: String)

    @ContractAction
    fun createCar(car: Car)

    @ContractAction
    fun setBlackListContract(contractId: String)
}

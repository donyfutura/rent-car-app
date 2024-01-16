package com.wavesenterprise.impl

import com.wavesenterprise.api.Car
import com.wavesenterprise.api.RentCarContract
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler
import com.wavesenterprise.sdk.contract.api.domain.ContractCall
import com.wavesenterprise.sdk.contract.api.state.ContractState
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping
import com.wavesenterprise.sdk.contract.core.state.getValue
import com.wavesenterprise.sdk.node.domain.contract.ContractId.Companion.base58ContractId

@ContractHandler
class RentCarContractImpl(
    val contractState: ContractState,
    val contractCall: ContractCall,
) : RentCarContract {

    val cars: Mapping<Car> by contractState

    override fun init() {
        val contractCreatorAddress = contractCall.sender.asBase58String()
        contractState.put(CONTRACT_CREATOR, contractCreatorAddress)
        cars.put(
            key = "1",
            value = Car(
                name = "bmw",
                number = 1,
            )
        )
        cars.put(
            key = "2",
            value = Car(
                name = "audi",
                number = 2,
            )
        )
        cars.put(
            key = "3",
            value = Car(
                name = "volvo",
                number = 3,
            )
        )
    }

    override fun rentCar(carNumber: Int) {
        val sender = contractCall.sender.asBase58String()
        checkInBlackList(sender)
        val car = getIfExist(carNumber)
        car.renter = sender
        car.date = contractCall.timestamp.utcTimestampMillis
        cars.put("$carNumber", car)
    }

    override fun changeCarRenter(carNumber: Int, renter: String) {
        isContractCreator()
        val car = getIfExist(carNumber)
        car.renter = renter
        cars.put("$carNumber", car)
    }

    override fun createCar(car: Car) {
        isContractCreator()
        cars.put("${car.number}", car)
    }

    override fun setBlackListContract(contractId: String) {
        isContractCreator()
        contractState.put(BLACK_LIST_CONTRACT_ID, contractId)
    }

    private fun isContractCreator() {
        val creator = contractState.get(CONTRACT_CREATOR, String::class.java)
        if (creator != contractCall.sender.asBase58String()) {
            throw IllegalArgumentException(
                "Sender with address ${contractCall.sender.asBase58String()} is not contract creator."
            )
        }
    }

    private fun getIfExist(carNumber: Int): Car {
        val car = cars.tryGet("$carNumber")
        return if (car.isPresent) {
            car.get()
        } else {
            throw IllegalArgumentException("Car with number $carNumber is not exist.")
        }
    }

    private fun checkInBlackList(address: String) {
        val blackListContractIdOptional = contractState.tryGet(BLACK_LIST_CONTRACT_ID, String::class.java)
        if (blackListContractIdOptional.isPresent) {
            val externalState = contractState.external(blackListContractIdOptional.get().base58ContractId)
            val itemOptional = externalState.tryGet("BLACK_LIST_$address", String::class.java)
            if (itemOptional.isPresent) {
                throw IllegalStateException("Sender with address $address exist in black list.")
            }
        }
    }

    companion object {
        const val CARS = "CARS_"
        const val CONTRACT_CREATOR = "CONTRACT_CREATOR"
        const val BLACK_LIST_CONTRACT_ID = "BLACK_LIST_CONTRACT_ID"
    }
}

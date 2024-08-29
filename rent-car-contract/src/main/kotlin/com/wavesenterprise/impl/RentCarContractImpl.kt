package com.wavesenterprise.impl

import com.wavesenterprise.api.Car
import com.wavesenterprise.api.RentCarContract
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler
import com.wavesenterprise.sdk.contract.api.domain.ContractCall
import com.wavesenterprise.sdk.contract.api.state.ContractState
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping
import com.wavesenterprise.sdk.contract.core.state.getValue
import com.wavesenterprise.sdk.node.domain.contract.ContractId.Companion.base58ContractId
import com.wavesenterprise.sdk.wrc.wrc10.WRC10RoleBasedAccessControl
import com.wavesenterprise.sdk.wrc.wrc10.impl.WRC10RoleBasedAccessControlImpl
import com.wavesenterprise.sdk.wrc.wrc10.impl.hasPermission

@ContractHandler
class RentCarContractImpl private constructor(
    val state: ContractState,
    val call: ContractCall,
    val accessControl: WRC10RoleBasedAccessControlImpl,
) : RentCarContract, WRC10RoleBasedAccessControl by accessControl {

    constructor(state: ContractState, call: ContractCall): this(
        state = state,
        call = call,
        accessControl = WRC10RoleBasedAccessControlImpl(state, call)
    )

    val cars: Mapping<Car> by state

    override fun create() {
        val contractCreatorAddress = call.caller
        accessControl.init()
        accessControl.grant(contractCreatorAddress, CONTRACT_CREATOR)
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
        val sender = call.caller
        checkInBlackList(sender)
        val car = getIfExist(carNumber)
        car.renter = sender
        car.date = call.timestamp.utcTimestampMillis
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
        state.put(BLACK_LIST_CONTRACT_ID, contractId)
    }

    private fun isContractCreator() {
        val caller = call.caller
        if (!accessControl.hasPermission(caller, CONTRACT_CREATOR)) {
            throw IllegalArgumentException(
                "Sender with address $caller is not contract creator."
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
        val blackListContractIdOptional = state.tryGet(BLACK_LIST_CONTRACT_ID, String::class.java)
        if (blackListContractIdOptional.isPresent) {
            val externalState = state.external(blackListContractIdOptional.get().base58ContractId)
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

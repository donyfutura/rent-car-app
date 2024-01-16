package com.wavesenterprise.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wavesenterprise.api.Car
import com.wavesenterprise.sdk.contract.api.domain.ContractCall
import com.wavesenterprise.sdk.contract.api.state.ContractState
import com.wavesenterprise.sdk.contract.test.state.ContractTestStateFactory
import com.wavesenterprise.sdk.node.domain.Address
import com.wavesenterprise.sdk.node.domain.Address.Companion.base58Address
import com.wavesenterprise.sdk.node.domain.Timestamp
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


class RentCarContractImplTest {

    @Test
    fun `should fill state when init`() {
        val contractCreator = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val state = ContractTestStateFactory.state(jacksonObjectMapper())
        val call: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns contractCreator
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = call)

        contract.init()

        assertTrue(state.results().isNotEmpty())
    }

    @Test
    fun `should change renter when rent by sender`() {
        val expectedAddress = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val state = ContractTestStateFactory.state(jacksonObjectMapper())
        state.put(RentCarContractImpl.CARS + 1, Car(name = "bmw", number = 1))
        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns expectedAddress
            every { it.timestamp } returns Timestamp.fromUtcTimestamp(1705402670887)
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)

        contract.rentCar(1)

        val car = state.get(RentCarContractImpl.CARS + 1, Car::class.java)

        assertTrue(car.renter == expectedAddress.asBase58String())
    }

    @Test
    fun `shouldn't change renter when rent by sender if car not exist`() {
        val expectedAddress = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val state = ContractTestStateFactory.state(jacksonObjectMapper())

        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns expectedAddress
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)
        val carNumber = 1
        assertThrows<IllegalArgumentException> { contract.rentCar(carNumber) }.apply {
            assertEquals("Car with number $carNumber is not exist.", this.message)
        }
    }

    @Test
    fun `should change renter by creator`() {
        val carNumber = 1
        val contractCreator = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val oldRenter = "3NoqQ88SuVBYuUimRWp3zeKLAYT66xLn5s3"
        val newRenter = "3NqNVU8XpEWLR86zvGAyZ6QL4xSse1EDb7K"
        val state = ContractTestStateFactory.state(jacksonObjectMapper())
        state.put(RentCarContractImpl.CARS + carNumber, Car(name = "bmw", number = carNumber, renter = oldRenter))
        state.put(RentCarContractImpl.CONTRACT_CREATOR, contractCreator.asBase58String())
        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns contractCreator
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)

        contract.changeCarRenter(carNumber, newRenter)

        state.get(RentCarContractImpl.CARS + carNumber, Car::class.java).apply {
            assertEquals(newRenter, this.renter)
        }
    }

    @Test
    fun `shouldn't change renter by not creator`() {
        val carNumber = 1
        val contractCreator = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val oldRenter = "3NoqQ88SuVBYuUimRWp3zeKLAYT66xLn5s3"
        val newRenter = "3NqNVU8XpEWLR86zvGAyZ6QL4xSse1EDb7K"
        val state = ContractTestStateFactory.state(jacksonObjectMapper())
        state.put(RentCarContractImpl.CARS + carNumber, Car(name = "bmw", number = carNumber, renter = oldRenter))

        state.put(RentCarContractImpl.CONTRACT_CREATOR, contractCreator)

        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns newRenter.base58Address
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)

        assertThrows<IllegalArgumentException> {
            contract.changeCarRenter(carNumber, newRenter)
        }.apply {
            assertEquals(
                "Sender with address ${contractCall.sender.asBase58String()} is not contract creator.", this.message
            )
        }
    }

    @Test
    fun `should create car by contract creator`() {
        val contractCreator = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val state = ContractTestStateFactory.state(jacksonObjectMapper())

        state.put(RentCarContractImpl.CONTRACT_CREATOR, contractCreator.asBase58String())
        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns contractCreator
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)

        val carNumber = 4
        contract.createCar(Car(name = "mersedes", number = carNumber))

        val car = state.get(RentCarContractImpl.CARS + carNumber, Car::class.java)

        assertTrue(car.number == carNumber)
    }

    @Test
    fun `shouldn't create car by not contract creator`() {
        val contractCreator = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val otherAddress = Address.fromBase58("3NoqQ88SuVBYuUimRWp3zeKLAYT66xLn5s3")
        val state = ContractTestStateFactory.state(jacksonObjectMapper())

        state.put(RentCarContractImpl.CONTRACT_CREATOR, contractCreator.asBase58String())
        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns otherAddress
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)

        val carNumber = 4
        assertThrows<IllegalArgumentException> {
            contract.createCar(Car(name = "mersedes", number = carNumber))
        }.apply {
            assertEquals(
                "Sender with address ${contractCall.sender.asBase58String()} is not contract creator.",
                this.message,
            )
        }
    }

    @Test
    fun `should set black list contract id by contract creator`() {
        val contractCreator = Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM")
        val state = ContractTestStateFactory.state(jacksonObjectMapper())

        state.put(RentCarContractImpl.CONTRACT_CREATOR, contractCreator.asBase58String())
        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns contractCreator
        }
        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)
        val blackListContractId = "2q4b2FsXnhEaFGxvvLRujbXG91CM7gxfXQdiEzdtDz8B"
        contract.setBlackListContract(contractId = blackListContractId)

        state.get(RentCarContractImpl.BLACK_LIST_CONTRACT_ID, String::class.java).apply {
            assertEquals(blackListContractId, this)
        }
    }

    @Test
    fun `shouldn't rent car when sender exist in black list contract`() {
        val blackListContractId = "2q4b2FsXnhEaFGxvvLRujbXG91CM7gxfXQdiEzdtDz8B"
        val otherAddress = Address.fromBase58("3NoqQ88SuVBYuUimRWp3zeKLAYT66xLn5s3")

        val externalState: ContractState = mockk<ContractState>().also {
            every { it.tryGet(any(), String::class.java) } returns Optional.of(otherAddress.asBase58String())
        }
        val state: ContractState = mockk<ContractState>().also {
            every { it.external(any()) } returns externalState
            every { it.tryGet(RentCarContractImpl.BLACK_LIST_CONTRACT_ID, String::class.java) } returns Optional.of(blackListContractId)
        }
        val contractCall: ContractCall = mockk<ContractCall>().also {
            every { it.sender } returns otherAddress
        }

        val contract = RentCarContractImpl(contractState = state, contractCall = contractCall)

        assertThrows<IllegalStateException> {
            contract.rentCar(1)
        }.apply {
            assertEquals("Sender with address ${otherAddress.asBase58String()} exist in black list.", this.message)
        }
    }
}

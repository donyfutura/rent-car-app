package com.wavesenterprise.rent.observer

import com.wavesenterprise.api.Car
import com.wavesenterprise.rent.entity.CarPublic
import com.wavesenterprise.rent.repository.CarPublicRepository
import com.wavesenterprise.we.tx.observer.api.key.KeyEvent
import com.wavesenterprise.we.tx.observer.api.key.KeyFilter
import com.wavesenterprise.we.tx.observer.api.tx.TxListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarPublicListener(
    private val carPublicRepository: CarPublicRepository,
) {

    var log = LoggerFactory.getLogger(CarPublicListener::class.java)

    @TxListener
    fun onCallRentCarContract(
        @KeyFilter(keyPrefix = "CARS_") keyEvent: KeyEvent<Car>
    ) {
        log.info("Received 104 tx with ID = ${keyEvent.tx.id.asBase58String()}")
        carPublicRepository.save(keyEvent.payload.toCarPublic())
    }

    private fun Car.toCarPublic() = CarPublic(
        number = number,
        renter = renter,
        name = name,
    )
}

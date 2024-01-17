package com.wavesenterprise.rent.observer

import com.wavesenterprise.rent.entity.CarPrivate
import com.wavesenterprise.rent.observer.config.MappingPrefix.POLICY_PREFIX
import com.wavesenterprise.rent.privacy.CarPrivacyContainer
import com.wavesenterprise.rent.repository.CarPrivateRepository
import com.wavesenterprise.we.tx.observer.api.privacy.PolicyFilter
import com.wavesenterprise.we.tx.observer.api.privacy.PrivateDataEvent
import com.wavesenterprise.we.tx.observer.api.tx.TxListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarPrivateListener(
    private val carPrivateRepository: CarPrivateRepository,
) {

    val log: Logger = LoggerFactory.getLogger(CarPrivateListener::class.java)

    @TxListener
    fun onCreateUserPrivacyContainer(
        @PolicyFilter(namePrefix = POLICY_PREFIX)
        privateDataEvent: PrivateDataEvent<CarPrivacyContainer>,
    ) {
        log.info("Received 114 tx with ID = ${privateDataEvent.policyDataHashTx.id.asBase58String()}")
        with(privateDataEvent) {
            carPrivateRepository.save(
                payload.toEntity(
                    txId = policyDataHashTx.id.asBase58String(),
                    policyId = policyDataHashTx.policyId.asBase58String(),
                )
            )
        }
    }

    private fun CarPrivacyContainer.toEntity(txId: String, policyId: String) =
        CarPrivate(
            number = number,
            renterPassportNumber = renterPassportNumber,
            txId = txId,
            policyId = policyId,
        )
}

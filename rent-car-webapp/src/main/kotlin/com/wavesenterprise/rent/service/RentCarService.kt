package com.wavesenterprise.rent.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wavesenterprise.api.BlackListContract
import com.wavesenterprise.api.Car
import com.wavesenterprise.api.RentCarContract
import com.wavesenterprise.rent.entity.CarPublic
import com.wavesenterprise.rent.entity.CarView
import com.wavesenterprise.rent.observer.config.MappingPrefix.POLICY_PREFIX
import com.wavesenterprise.rent.privacy.CarPrivacyContainer
import com.wavesenterprise.rent.repository.CarPublicRepository
import com.wavesenterprise.rent.repository.CarViewRepository
import com.wavesenterprise.sdk.contract.client.invocation.factory.ContractBlockingClientFactory
import com.wavesenterprise.sdk.node.client.blocking.credentials.NodeCredentialsProvider
import com.wavesenterprise.sdk.node.client.blocking.node.NodeInfoService
import com.wavesenterprise.sdk.node.client.blocking.privacy.PrivacyService
import com.wavesenterprise.sdk.node.client.blocking.tx.TxService
import com.wavesenterprise.sdk.node.domain.Address
import com.wavesenterprise.sdk.node.domain.DataSize
import com.wavesenterprise.sdk.node.domain.Fee
import com.wavesenterprise.sdk.node.domain.FileName
import com.wavesenterprise.sdk.node.domain.Hash
import com.wavesenterprise.sdk.node.domain.PolicyDescription
import com.wavesenterprise.sdk.node.domain.PolicyId
import com.wavesenterprise.sdk.node.domain.PolicyId.Companion.policyId
import com.wavesenterprise.sdk.node.domain.PolicyName
import com.wavesenterprise.sdk.node.domain.Timestamp
import com.wavesenterprise.sdk.node.domain.TxVersion
import com.wavesenterprise.sdk.node.domain.base58.WeBase58
import com.wavesenterprise.sdk.node.domain.contract.ContractId
import com.wavesenterprise.sdk.node.domain.privacy.Data
import com.wavesenterprise.sdk.node.domain.privacy.DataAuthor
import com.wavesenterprise.sdk.node.domain.privacy.DataComment
import com.wavesenterprise.sdk.node.domain.privacy.PolicyItemFileInfo
import com.wavesenterprise.sdk.node.domain.privacy.SendDataRequest
import com.wavesenterprise.sdk.node.domain.sign.CreatePolicySignRequest
import com.wavesenterprise.sdk.spring.autoconfigure.atomic.annotation.Atomic
import com.wavesenterprise.sdk.tx.signer.TxSigner
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant

@Service
class RentCarService(
    private val rentCarContract: ContractBlockingClientFactory<RentCarContract>,
    private val blackListContract: ContractBlockingClientFactory<BlackListContract>,
    @Value("\${contracts.config.rentCarContract.contract-id}")
    private val rentCarContractId: String,
    @Value("\${contracts.config.blackListContract.contract-id}")
    private val blackListContractId: String,
    private val carPublicRepository: CarPublicRepository,
    private val carViewRepository: CarViewRepository,
    private val txService: TxService,
    private val privacyService: PrivacyService,
    private val nodeInfoService: NodeInfoService,
    private val txSigner: TxSigner,
    private val credentialsProvider: NodeCredentialsProvider,
) {

    private val objectMapper = jacksonObjectMapper()

    fun initRent(): String {
        val (tx) = rentCarContract.executeContract {
            it.init()
        }
        return tx.id.asBase58String()
    }

    fun getFree(): List<CarPublic> = carPublicRepository.findAll()

    fun getRented(): List<CarView> = carViewRepository.findAll()

    fun createCar(car: Car): String {
        val (tx) = rentCarContract.executeContract(
            ContractId.fromBase58(rentCarContractId)
        ) {
            it.createCar(car)
        }
        return tx.id.asBase58String()
    }

    @Atomic
    fun rentCar(carNumber: Int, renterPassportNumber: String): String {

        // Rent car with contract (104 TX)
        val (tx) = rentCarContract.executeContract(
            ContractId.fromBase58(rentCarContractId)
        ) {
            it.rentCar(carNumber)
        }

        // Policy creation (112 TX)
        val createPolicyTx = createPolicyTx(carNumber)
        txService.broadcast(createPolicyTx)

        // Sending private data to policy (114 TX)
        val carPrivacyContainer = CarPrivacyContainer(
            number = carNumber,
            renterPassportNumber = renterPassportNumber,
        )
        val data = encodeToBase64String(carPrivacyContainer)
        val dataHash = getDataHash(carPrivacyContainer)
        val nodeOwner = nodeInfoService.getNodeOwner().address
        policyDataHashTx(
            nodeOwner = nodeOwner,
            policyId = createPolicyTx.id.policyId,
            dataHash = dataHash,
            data = data,
            carNumber = carNumber,
        ).also {
            txService.broadcast(it)
        }

        return tx.id.asBase58String()
    }

    private fun createPolicyTx(carNumber: Int) = txSigner.sign(
        CreatePolicySignRequest(
            version = TxVersion(3),
            senderAddress = Address.EMPTY,
            fee = Fee(0),
            feeAssetId = null,
            policyName = PolicyName("$POLICY_PREFIX$carNumber"),
            recipients = ADDRESSES,
            owners = ADDRESSES,
            description = PolicyDescription("rent_demo"),
        )
    )

    private fun encodeToBase64String(privacyContainer: Any): String =
        Base64.encodeBase64String(objectMapper.writeValueAsBytes(privacyContainer))

    private fun getDataHash(privacyContainer: Any) =
        WeBase58.encode(
            MessageDigest.getInstance("SHA-256").digest((objectMapper.writeValueAsBytes(privacyContainer)))
        )

    private fun policyDataHashTx(
        nodeOwner: Address,
        policyId: PolicyId,
        dataHash: String,
        data: String,
        carNumber: Int,
    ) = privacyService.sendData(
        request = SendDataRequest(
            senderAddress = nodeOwner,
            policyId = policyId,
            dataHash = Hash.fromStringBase58(dataHash),
            data = Data.fromBase64(data),
            info = PolicyItemFileInfo(
                filename = FileName("car_$carNumber"),
                size = DataSize(Base64.decodeBase64(data).size.toLong()),
                timestamp = Timestamp.fromUtcTimestamp(Instant.now().toEpochMilli()),
                author = DataAuthor("test"),
                comment = DataComment(objectMapper.writeValueAsString(mapOf(OPERATION_KEY to ADD_OPERATION))),
            ),
            fee = Fee(0),
            feeAssetId = null,
            broadcastTx = false,
            version = TxVersion(3),
            password = credentialsProvider.getPassword(nodeOwner)
        )
    )

    companion object {
        const val OPERATION_KEY = "OPERATION"
        const val ADD_OPERATION = "ADD"
        private val ADDRESSES = listOf(
            Address.fromBase58("3NqmRauaV87hhJPz1wzS6wx8kqWD5i7coCM"),
        )
    }
}

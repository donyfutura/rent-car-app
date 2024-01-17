package com.wavesenterprise.rent.observer.config

import com.wavesenterprise.rent.observer.config.MappingPrefix.POLICY_PREFIX
import com.wavesenterprise.sdk.node.client.blocking.contract.ContractService
import com.wavesenterprise.sdk.node.client.blocking.tx.TxService
import com.wavesenterprise.sdk.node.domain.contract.ContractId.Companion.base58ContractId
import com.wavesenterprise.sdk.node.domain.tx.AtomicTx
import com.wavesenterprise.sdk.node.domain.tx.CallContractTx
import com.wavesenterprise.sdk.node.domain.tx.CreateContractTx
import com.wavesenterprise.sdk.node.domain.tx.CreatePolicyTx
import com.wavesenterprise.sdk.node.domain.tx.ExecutableTx
import com.wavesenterprise.sdk.node.domain.tx.ExecutedContractTx
import com.wavesenterprise.sdk.node.domain.tx.PolicyDataHashTx
import com.wavesenterprise.sdk.node.domain.tx.Tx
import com.wavesenterprise.sdk.node.domain.tx.UpdateContractTx
import com.wavesenterprise.we.tx.observer.api.tx.TxEnqueuePredicate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RentCarPredicate(
    private val txService: TxService,
    private val contractService: ContractService,
    @Value("\${contracts.config.rentCarContract.image}")
    val rentCarContractImage: String,
    @Value("\${contracts.config.rentCarContract.contract-id}")
    private val rentCarContractId: String,
) : TxEnqueuePredicate {

    override fun isEnqueued(tx: Tx): Boolean = when (tx) {
        is PolicyDataHashTx -> filterForPolicyDataHashTx(tx)
        is ExecutedContractTx -> {
            rentCarContractImage == tx.tx.image()
                    || tx.contractId().base58ContractId == rentCarContractId.base58ContractId
        }

        else -> false
    }

    private fun ExecutableTx.image(): String =
        when (this) {
            is CallContractTx -> contractService.getContractInfo(contractId).get().image.value
            is CreateContractTx -> image.value
            is UpdateContractTx -> image.value
        }

    private fun ExecutedContractTx.contractId(): String =
        when (val tx = tx) {
            is CallContractTx -> tx.contractId.asBase58String()
            is CreateContractTx -> tx.id.asBase58String()
            is UpdateContractTx -> tx.contractId.asBase58String()
        }

    private fun filterForPolicyDataHashTx(tx: PolicyDataHashTx): Boolean {
        val policyTx = txService.txInfo(tx.policyId.txId).get().tx as CreatePolicyTx
        return filterForPolicyTx(policyTx)
    }

    private fun filterForPolicyTx(policyTx: CreatePolicyTx) =
        policyTx.policyName.value.startsWith(POLICY_PREFIX)
}

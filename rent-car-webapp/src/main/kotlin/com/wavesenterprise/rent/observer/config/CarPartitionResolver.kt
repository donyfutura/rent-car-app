package com.wavesenterprise.rent.observer.config

import com.wavesenterprise.rent.observer.config.MappingPrefix.CONTRACT_MAPPING_PREFIX
import com.wavesenterprise.rent.observer.config.MappingPrefix.POLICY_PREFIX
import com.wavesenterprise.sdk.node.client.blocking.tx.TxService
import com.wavesenterprise.sdk.node.domain.tx.CreatePolicyTx
import com.wavesenterprise.sdk.node.domain.tx.ExecutedContractTx
import com.wavesenterprise.sdk.node.domain.tx.PolicyDataHashTx
import com.wavesenterprise.sdk.node.domain.tx.Tx
import com.wavesenterprise.we.tx.observer.api.partition.TxQueuePartitionResolver
import org.springframework.stereotype.Component

@Component
class CarPartitionResolver(
    private val txService: TxService,
) : TxQueuePartitionResolver {
    override fun resolvePartitionId(tx: Tx): String? = when (tx) {
        is PolicyDataHashTx -> resolveForPolicyDataHashTx(tx)
        is ExecutedContractTx -> resolveForContractTx(tx)
        else -> null
    }

    private fun resolveForPolicyDataHashTx(policyDataHashTx: PolicyDataHashTx): String? {
        val createPolicyTx = txService.txInfo(policyDataHashTx.policyId.txId).get().tx as CreatePolicyTx
        return createPolicyTx.policyName.value
            .takeIf { policyName -> policyName.startsWith(POLICY_PREFIX) }
            ?.removePrefix(POLICY_PREFIX)
    }

    private fun resolveForContractTx(executedContractTx: ExecutedContractTx): String? =
        executedContractTx.results.map { param -> param.key.value }
            .find { paramKey -> paramKey.startsWith(CONTRACT_MAPPING_PREFIX) }
            ?.removePrefix(CONTRACT_MAPPING_PREFIX)
}

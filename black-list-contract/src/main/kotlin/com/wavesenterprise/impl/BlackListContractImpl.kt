package com.wavesenterprise.impl

import com.wavesenterprise.api.BlackListContract
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler
import com.wavesenterprise.sdk.contract.api.state.ContractState
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping
import com.wavesenterprise.sdk.contract.core.state.getValue

@ContractHandler
class BlackListContractImpl(
    val contractState: ContractState,
) : BlackListContract{

    val blackList: Mapping<String> by contractState

    override fun init() { }

    override fun addRenter(address: String) {
        blackList.put(address, address)
    }
}

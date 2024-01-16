package com.wavesenterprise.api

import com.wavesenterprise.sdk.contract.api.annotation.ContractAction
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit
import com.wavesenterprise.sdk.contract.api.annotation.InvokeParam

interface BlackListContract {

    @ContractInit
    fun init()

    @ContractAction
    fun addRenter(@InvokeParam("address") address: String)
}
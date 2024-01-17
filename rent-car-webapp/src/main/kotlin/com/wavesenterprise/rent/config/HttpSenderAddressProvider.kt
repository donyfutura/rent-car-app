package com.wavesenterprise.rent.config

import com.wavesenterprise.sdk.node.client.blocking.credentials.SenderAddressProvider
import com.wavesenterprise.sdk.node.domain.Address
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

class HttpSenderAddressProvider : SenderAddressProvider {

    override fun address(): Address {
        return Address.fromBase58(getRequest().getHeader(TX_SENDER_HEADER_NAME))
    }

    private fun getRequest(): HttpServletRequest {
        val attribs = RequestContextHolder.getRequestAttributes()
        if (attribs != null) {
            return (attribs as ServletRequestAttributes).request
        }
        throw IllegalArgumentException("Request must not be null!")
    }

    companion object {
        const val TX_SENDER_HEADER_NAME = "X-Tx-Sender"
    }
}

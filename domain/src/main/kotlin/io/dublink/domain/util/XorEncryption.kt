package io.dublink.domain.util

import java.util.Base64
import kotlin.experimental.xor

object XorEncryption {

    fun decode(s: String, key: String): String {
        return String(xorWithKey(base64Decode(s), key.toByteArray()))
    }

    private fun xorWithKey(a: ByteArray, key: ByteArray): ByteArray {
        val out = ByteArray(a.size)
        for (i in a.indices) {
            out[i] = (a[i] xor key[i % key.size])
        }
        return out
    }

    private fun base64Decode(s: String): ByteArray {
        return Base64.getDecoder().decode(s)
    }
}

package io.dublink.domain.util

import java.util.Base64
import kotlin.experimental.xor

object XorEncryption {

    val key = listOf('b', 'Q', 'x', 'p', 'N', 'n', 'x', 'E', 'A', 'F', 'z', 'J', 'p', 's', 'B', 'y')
        .map { char -> char.toByte() }
        .toByteArray()

    fun decode(s: String): String = String(xorWithKey(base64Decode(s)))

    private fun xorWithKey(a: ByteArray): ByteArray {
        val out = ByteArray(a.size)
        for (i in a.indices) {
            out[i] = (a[i] xor key[i % key.size])
        }
        return out
    }

    private fun base64Decode(s: String): ByteArray = Base64.getDecoder().decode(s)
}

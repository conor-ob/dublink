package io.dublink.iap

import android.util.Base64
import com.android.billingclient.api.Purchase
import timber.log.Timber
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import javax.inject.Inject

private const val keyFactoryAlgorithm = "RSA"
private const val signatureAlgorithm = "SHA1withRSA"

class InAppPurchaseVerifier @Inject constructor(
    private val encodedPublicKey: String
) {

    fun verifyPurchase(purchase: Purchase): Boolean {
        val signedData = purchase.originalJson
        val signature = purchase.signature
        if (encodedPublicKey.isEmpty() || signedData.isEmpty() || signature.isEmpty()) {
        Timber.w("Purchase verification failed: missing data")
            return false
        }
        val publicKey = generatePublicKey()
        return verify(publicKey, signedData, signature)
    }

    private fun generatePublicKey(): PublicKey {
        try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(keyFactoryAlgorithm)
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            val msg = "Invalid key specification: $e"
            Timber.w(msg)
            throw IOException(msg)
        }
    }

    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        val signatureBytes: ByteArray
        try {
            signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Timber.w("Base64 decoding failed")
            return false
        }
        try {
            val signatureAlgorithm = Signature.getInstance(signatureAlgorithm)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            if (!signatureAlgorithm.verify(signatureBytes)) {
                Timber.w("Signature verification failed")
                return false
            }
            return true
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            Timber.w("Invalid key specification")
        } catch (e: SignatureException) {
            Timber.w("Signature exception")
        }
        return false
    }
}

package com.example.touchauth.ui

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.example.touchauth.R
import com.example.touchauth.common.BiometricAuthListener
import com.example.touchauth.common.EncryptedMessage
import com.example.touchauth.presenter.StatePresenter
import com.example.touchauth.util.Biometric
import com.example.touchauth.util.Cryptography
import com.example.touchauth.util.Preference
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_pin_code.*
import javax.crypto.Cipher

class PinCodeActivity : AppCompatActivity(), BiometricAuthListener {

    private var encryptedMessages : EncryptedMessage? = null
    private val keyName = "T0UCH_AUTH33"
    private val pinCodeList = mutableListOf<String>()
    private val statePres : StatePresenter = StatePresenter()

   override fun onCreate(savedInstanceState : Bundle?) {
      super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_pin_code)
       supportActionBar?.hide()

       statePres.init(this)
       checkSavePreference()
       initListeners()
   }

    private fun initListeners() {
        pin_code_number_1.setOnClickListener { statePres.addPin("1") }
        pin_code_number_2.setOnClickListener { statePres.addPin("2") }
        pin_code_number_3.setOnClickListener { statePres.addPin("3") }
        pin_code_number_4.setOnClickListener { statePres.addPin("4") }
        pin_code_number_5.setOnClickListener { statePres.addPin("5") }
        pin_code_number_6.setOnClickListener { statePres.addPin("6") }
        pin_code_number_7.setOnClickListener { statePres.addPin("7") }
        pin_code_number_8.setOnClickListener { statePres.addPin("8") }
        pin_code_number_9.setOnClickListener { statePres.addPin("9") }
        pin_code_number_0.setOnClickListener { statePres.addPin("0") }
        pin_code_backspace.setOnClickListener { statePres.backspace() }
    }

    private fun checkSavePreference() {
        val message = Preference.getMessageList(this)
        if (!message.isNullOrEmpty()) {
            statePres.setStateStep(state = 2)
            encryptedMessages = message[0]
        } else statePres.setStateStep(state = 0)
    }

    private fun setPassPreference() : Cipher =
        Cryptography.getInitializedCipherForEncryption(keyName, false)
        //Cryptography.getInitializedCipherForDecryption(keyName, encryptedMessages?.iv, false)

    fun getSavePreference() : String {
        val decryptPass = Preference.getEncryptedMessage(this, encryptedMessages?.savedAt.toString())
        val cipher = Cryptography.getInitializedCipherForDecryption(keyName, encryptedMessages?.iv, false)
        return Cryptography.decryptData(decryptPass!!.cipherText, cipher)
    }

    fun snackBarVis(message : String) {
        Snackbar.make(pinView, message, Snackbar.LENGTH_LONG).show()
    }

    fun biometryStart() {
        if (Biometric.isBiometricReady(this))
            showBiometricPromptToEncrypt()
        else showAlertToSetupBiometric()
    }

    private fun showAlertToSetupBiometric() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.message_encryption_failed))
            .setMessage(getString(R.string.message_no_biometric))
            .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                Biometric.lunchBiometricSettings(this)
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()
    }

    private fun showBiometricPromptToEncrypt() {
        val cryptoObject = BiometricPrompt.CryptoObject(
            Cryptography.getInitializedCipherForEncryption(keyName, false)
        )
        Biometric.showBiometricPrompt(
            activity = this,
            listener = this,
            cryptoObject = cryptoObject,
            allowDeviceCredential = true
        )
    }

    fun showBiometricPromptToDecrypt() {
        encryptedMessages?.iv?.let { it ->
            val cryptoObject = BiometricPrompt.CryptoObject(
                Cryptography.getInitializedCipherForDecryption(
                    keyName, it, false
                )
            )
            // Show BiometricPrompt With Cryptography Object
            Biometric.showBiometricPrompt(
                activity = this,
                listener = this,
                cryptoObject = cryptoObject,
                allowDeviceCredential = true
            )
        }
    }

    fun changePinsView(listPoint : MutableList<String>) {
        val activePoint = R.drawable.circle_blue
        val disablePoint = R.drawable.circle_white_blue_border

        val point1 = if (listPoint.getOrNull(0).isNullOrEmpty()) disablePoint
        else activePoint
        val point2 = if (listPoint.getOrNull(1).isNullOrEmpty()) disablePoint
        else activePoint
        val point3 = if (listPoint.getOrNull(2).isNullOrEmpty()) disablePoint
        else activePoint
        val point4 = if (listPoint.getOrNull(3).isNullOrEmpty()) disablePoint
        else activePoint
        val point5 = if (listPoint.getOrNull(4).isNullOrEmpty()) disablePoint
        else activePoint
        val point6 = if (listPoint.getOrNull(5).isNullOrEmpty()) disablePoint
        else activePoint

        pin_code_pin1.setBackgroundResource(point1)
        pin_code_pin2.setBackgroundResource(point2)
        pin_code_pin3.setBackgroundResource(point3)
        pin_code_pin4.setBackgroundResource(point4)
        pin_code_pin5.setBackgroundResource(point5)
        pin_code_pin6.setBackgroundResource(point6)
    }

    fun clearPoint() {
        val disablePoint = R.drawable.circle_white_blue_border

        disablePoint.also {
            pin_code_pin1.setBackgroundResource(it)
            pin_code_pin2.setBackgroundResource(it)
            pin_code_pin3.setBackgroundResource(it)
            pin_code_pin4.setBackgroundResource(it)
            pin_code_pin5.setBackgroundResource(it)
            pin_code_pin6.setBackgroundResource(it)
        }
    }

    fun setVisibilityThdState() {
        View.GONE.also {
            line.visibility = it
            pin_code_title.visibility = it
            pin_code_number_1.visibility = it
            pin_code_number_2.visibility = it
            pin_code_number_3.visibility = it
            pin_code_number_4.visibility = it
            pin_code_number_5.visibility = it
            pin_code_number_6.visibility = it
            pin_code_number_7.visibility = it
            pin_code_number_8.visibility = it
            pin_code_number_9.visibility = it
            pin_code_number_0.visibility = it
            pin_code_backspace.visibility = it
            pin_code_biometric.visibility = it
            pin_code_contaner_pin_1.visibility = it
        }
        entry_success.visibility = View.VISIBLE
    }

    fun savePinCodeHash() {
        val message = pinCodeList.joinToString(separator = "")
        val cipher = setPassPreference()
        if (!TextUtils.isEmpty(message)) {
            encryptAndSave(message, cipher)
            statePres.setStateStep(state = 2)
        }
    }

    private fun encryptAndSave(plainTextMessage: String, cipher: Cipher) {
        val encryptedMessage = Cryptography.encryptData(plainTextMessage, cipher)
        encryptedMessages = encryptedMessage
        Preference.storeEncryptedMessage(
            applicationContext,
            prefKey = encryptedMessage.savedAt.toString(),
            encryptedMessage = encryptedMessage
        )
    }

    private fun decryptAndDisplay(cipher: Cipher?) {
        encryptedMessages?.cipherText?.let { it ->
            val decryptedMessage = Cryptography.decryptData(it, cipher)
            val storePass = getSavePreference()
            if (storePass == decryptedMessage) statePres.setStateStep(state = 3)
            else snackBarVis("Error: Biometry is wrong!")
        }
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        if (statePres.getStateStep() == 1) {
            val message = pinCodeList.joinToString(separator = "")
            result.cryptoObject?.cipher?.let {
                if (!TextUtils.isEmpty(message)) encryptAndSave(message, it)
            }
        } else if (statePres.getStateStep() == 2) {
            result.cryptoObject?.cipher?.let { decryptAndDisplay(it) }
            statePres.setStateStep(state = 3)
        }
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {
        snackBarVis("Biometric error: $errorMessage")
    }

    fun setStateView(state : Int) {
        when (state) {
            0 -> { pin_code_title.text = getString(R.string.create_pin) }
            1 -> { pin_code_title.text = getString(R.string.confirm_pin) }
            2 -> {
                pin_code_title.text = getString(R.string.auth_pin)
                pin_code_biometric.visibility = View.VISIBLE
                encryptedMessages = intent.extras?.getParcelable(getString(R.string.parcel_message))
            }
        }
    }
}
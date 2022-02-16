package com.example.touchauth.presenter

import com.example.touchauth.ui.PinCodeActivity

class StatePresenter : States {

    private var state = 0
    private var indexError = 0
    private val pinCodeList = mutableListOf<String>()
    private val pinCodeConfirmList = mutableListOf<String>()
    private val pinCodeEntryList = mutableListOf<String>()
    private var pinActivity : PinCodeActivity? = null

    override fun init(pinActivity: PinCodeActivity) {
        this.pinActivity = pinActivity
    }

    override fun getStateStep(): Int = state

    override fun setStateStep(state: Int) {
        when (state) {
            0 -> {
                this.state = state
                pinActivity?.clearPoint()
                pinActivity?.setStateView(state)
            }   //create PIN code
            1 -> {
                this.state = state
                pinActivity?.clearPoint()
                pinActivity?.setStateView(state)
            }   //confirm PIN code
            2 -> {
                this.state = state
                pinActivity?.clearPoint()
                pinActivity?.setStateView(state)
                pinActivity?.showBiometricPromptToDecrypt()
            }  //entry PIN code
            else -> {
                this.state = state
                pinActivity?.setVisibilityThdState()
            }  //entry success
        }
    }

    override fun addPin(char: String) {
        when (getStateStep()) {
            0 -> {
                if (pinCodeList.count() < 6) {
                    pinCodeList.add(char)
                    pinActivity?.changePinsView(pinCodeList)
                }
                if (pinCodeList.count() == 6) setStateStep(state = 1)
            }
            1 -> {
                if (pinCodeConfirmList.count() < 6) {
                    pinCodeConfirmList.add(char)
                    pinActivity?.changePinsView(pinCodeConfirmList)
                }
                if (pinCodeConfirmList.count() == 6) {
                    if (pinCodeList.joinToString(separator = "") == pinCodeConfirmList.joinToString(separator = "")) {
                        pinActivity?.savePinCodeHash()
                        pinActivity?.biometryStart()
                        setStateStep(state = 2)
                        return
                    } else {
                        if (getStateStep() == 1) {
                            pinActivity?.snackBarVis("Password is wrong!")
                            pinCodeConfirmList.clear()
                            pinActivity?.changePinsView(pinCodeConfirmList)
                        }
                    }
                }
            }
            2 -> {
                if (pinCodeEntryList.count() < 6) {
                    pinCodeEntryList.add(char)
                    pinActivity?.changePinsView(pinCodeEntryList)
                }
                if (pinCodeEntryList.count() == 6) {
                    val storePass = pinActivity?.getSavePreference()
                    if (storePass == pinCodeEntryList.joinToString(separator = "")) {
                        setStateStep(state = 3)
                        return
                    }
                    else {
                        pinActivity?.snackBarVis("Password is wrong!")
                        pinCodeEntryList.clear()
                        pinActivity?.changePinsView(pinCodeEntryList)
                    }
                }
            }
            else -> {
                if (pinCodeList.count() == 6 && getStateStep() != 0) {
                    when {
                        indexError < 2 -> {
                            pinActivity?.snackBarVis("Enter your password!")
                            indexError++
                            pinCodeList.clear()
                            pinActivity?.changePinsView(pinCodeList)
                        }
                    }
                }
            }
        }
    }

    override fun backspace() {
        when (getStateStep()) {
            0 -> {
                if (pinCodeList.count() > 0)
                    pinCodeList.removeAt(pinCodeList.count() - 1)
                pinActivity?.changePinsView(pinCodeList)
            }
            1 -> {
                if (pinCodeConfirmList.count() > 0)
                    pinCodeConfirmList.removeAt(pinCodeConfirmList.count() - 1)
                pinActivity?.changePinsView(pinCodeConfirmList)
            }
            2 -> {
                if (pinCodeEntryList.count() > 0)
                    pinCodeEntryList.removeAt(pinCodeEntryList.count() - 1)
                pinActivity?.changePinsView(pinCodeEntryList)
            }
            else -> return
        }
    }
}
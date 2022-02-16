package com.example.touchauth.presenter

import com.example.touchauth.ui.PinCodeActivity

interface States {

    fun init(pinActivity : PinCodeActivity)
    fun getStateStep() : Int
    fun setStateStep(state : Int)
    fun addPin(char: String)
    fun backspace()
}
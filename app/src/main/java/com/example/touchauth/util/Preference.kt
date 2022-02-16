package com.example.touchauth.util

import android.content.Context
import com.example.touchauth.common.EncryptedMessage
import com.google.gson.Gson

object Preference {

    private const val sharedPrefsFileName = "biometric_prefs"

    fun storeEncryptedMessage(
        context : Context,
        prefKey : String,
        encryptedMessage : EncryptedMessage
    ) {
        val json = Gson().toJson(encryptedMessage)
        context.getSharedPreferences(sharedPrefsFileName, Context.MODE_PRIVATE)
            .edit()
            .putString(prefKey, json).apply()
    }

    fun getEncryptedMessage(
        context : Context,
        prefKey : String
    ) : EncryptedMessage? {
        val json = context.getSharedPreferences(sharedPrefsFileName, Context.MODE_PRIVATE)
            .getString(prefKey, null)
        return Gson().fromJson(json, EncryptedMessage::class.java)
    }

    fun getMessageList(context : Context) : List<EncryptedMessage> {
        return context.getSharedPreferences(sharedPrefsFileName, Context.MODE_PRIVATE)
            .all
            .map { Gson().fromJson(it.value as String, EncryptedMessage::class.java) }
            .sortedBy { it.savedAt }
            .reversed()
    }
}
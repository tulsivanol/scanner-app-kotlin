package com.tulsivanol.coder.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(internal var context: Context){
    internal var pref:SharedPreferences
    internal var editor:SharedPreferences.Editor

    internal var PRIVATE_MODE=0

    companion object {
        // Shared preferences file name
        private val PREF_NAME = "tulsivanol"
    }

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun setUid(uid: String) {
        editor.putString("uid", uid)
        editor.commit()
    }

    fun getUid(): String? {
        return pref.getString("uid", "")
    }

    fun  getEmail() :String?{
        return pref.getString("email","")
    }

    fun setEmail(email:String?){
        editor.putString("email",email)
        editor.commit()
    }

    fun setName(name: String) {
        editor.putString("name", name)
        editor.commit()
    }

    fun getName(): String? {
        return pref.getString("name", "")
    }

    fun setToken(token: String) {
        editor.putString("token", token)
        editor.commit()
    }

    fun getToken(): String? {
        return pref.getString("token", "")
    }


}
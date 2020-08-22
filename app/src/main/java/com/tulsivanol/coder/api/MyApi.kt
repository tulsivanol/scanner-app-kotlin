package com.tulsivanol.coder.api

import android.content.Context
import com.tulsivanol.coder.model.*
import com.tulsivanol.coder.utils.Helper
import com.tulsivanol.coder.utils.PrefManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MyApi {

    @POST("login")
    suspend fun loginUser(
        @Body userCredentials: LoginCredentials
    ): Response<User>

    @POST("item-store")
    suspend fun sendQRCodeResult(
        @HeaderMap headers:HashMap<String,String>,
        @Body qrCodeData: QRCodeData
    ): Response<ResponseBody>

}
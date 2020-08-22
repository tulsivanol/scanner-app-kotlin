package com.tulsivanol.coder.api

import com.tulsivanol.coder.model.*
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
        @Body qrCodeData: QRCodeData
    ): Response<QRCodeResponse>

}
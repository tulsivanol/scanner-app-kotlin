package com.tulsivanol.coder.ui.activities

import android.app.Dialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.tulsivanol.coder.R
import com.tulsivanol.coder.api.MyApi
import com.tulsivanol.coder.api.MyInstance
import com.tulsivanol.coder.model.QRCodeData
import com.tulsivanol.coder.utils.Helper
import com.tulsivanol.coder.utils.PrefManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private lateinit var codeScanner: CodeScanner
    private lateinit var retrofit: MyApi
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefManager = PrefManager(this)
        codeScanner = CodeScanner(this@MainActivity, scanner_view)
        retrofit = MyInstance.getApi

//        camera setup
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

//        callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                sendQRCodeToApi(it.text)
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Helper.showToast(it.message.toString(), this)
            }
        }

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun sendQRCodeToApi(qrCode: String?) {
        var progressDialog: Dialog? = null
        progressDialog = Helper.showDialog(this@MainActivity)
        progressDialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            val headers =
                HashMap<String, String>()
            headers["Authorization"] = "Bearer ${prefManager.getToken()}"
            val response = retrofit.sendQRCodeResult(headers, QRCodeData("8597603342225876"))
            if (response.code() == 401) {
                withContext(Dispatchers.Main) {
                    try {
                        var resStr: String? = null
                        if (response.body() != null) {
                            resStr = response.body()?.string()
                        } else {
                            resStr = response.errorBody()?.string()
                        }
                        val json: JSONObject? = JSONObject(resStr!!)
                        progressDialog.dismiss()
                        Log.d(TAG, "sendQRCodeToApi: $resStr")
                        if (json!!.has("success")) {
                            val dialog = Helper.showAlertDialogWithoutBtn(
                                this@MainActivity,
                                json!!.getString("success")
                            )
                            dialog.show()
                            delay(1000)
                            dialog.hide()
                            codeScanner.releaseResources()
                            codeScanner.startPreview()
                        } else if (json!!.has("error")) {
                            val dialog = Helper.showAlertDialogWithBtn(
                                this@MainActivity,
                                json!!.getString("error"),
                                codeScanner
                            )
                            dialog.show()
                        } else {
                            Log.d(TAG, "sendQRCodeToApi: error")
                            codeScanner.releaseResources()
                            codeScanner.startPreview()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    try {
                        val resStr = response.errorBody()?.string()
                        val json: JSONObject? = JSONObject(resStr!!)
                        if (json!!.has("error")) {
                            val dialog = Helper.showAlertDialogWithBtn(
                                this@MainActivity,
                                json!!.getString("error"),
                                codeScanner
                            )
                            dialog.show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

    override fun onStart() {
        super.onStart()
        if (prefManager.getUid().isNullOrEmpty()) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java).also {
                it.addFlags(FLAG_ACTIVITY_NEW_TASK)
                it.addFlags(FLAG_ACTIVITY_CLEAR_TASK)
            })
            finish()
            finishAffinity()
        }
    }
}
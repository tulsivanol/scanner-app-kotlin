package com.tulsivanol.coder.ui.activities

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.tulsivanol.coder.R
import com.tulsivanol.coder.api.MyApi
import com.tulsivanol.coder.api.MyInstance
import com.tulsivanol.coder.constants.Constants
import com.tulsivanol.coder.model.QRCodeData
import com.tulsivanol.coder.utils.Helper
import com.tulsivanol.coder.utils.PrefManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                Helper.showToast(it.text, this)
                CoroutineScope(Dispatchers.IO).launch {
                    sendQRCodeToApi(it.text)
                }
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

        requestCameraPermission()
    }

    private suspend fun sendQRCodeToApi(qrCode: String?) {
        withContext(Dispatchers.IO){
            val response = retrofit.sendQRCodeResult(QRCodeData(qrCode!!))
            if (response.isSuccessful){
                Helper.showToast("Something went wrong ${response.body()!!.success}",this@MainActivity)
                Helper.showToast("Something went wrong ${response.body()!!.error}",this@MainActivity)
                Log.d(TAG, "sendQRCodeToApi: ${response.body()!!.success}")
                Log.d(TAG, "sendQRCodeToApi: ${response.body()!!.error}")
            }else{
                withContext(Dispatchers.Main){
                    Helper.showToast("Something went wrong ${response.message()}",this@MainActivity)
                }
            }
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.CAMERA
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), Constants.REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), Constants.REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {
                        Helper.showToast("Permission Granted", this@MainActivity)
                    } else {
                        Helper.showToast(
                            "Permission Denied.Please grant permission",
                            this@MainActivity
                        )
                    }
                    return
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
package com.tulsivanol.coder.ui.activities

import android.app.Dialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tulsivanol.coder.R
import com.tulsivanol.coder.api.MyApi
import com.tulsivanol.coder.api.MyInstance
import com.tulsivanol.coder.model.LoginCredentials
import com.tulsivanol.coder.model.User
import com.tulsivanol.coder.utils.Helper
import com.tulsivanol.coder.utils.PrefManager
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var retrofit: MyApi
    private lateinit var prefManager: PrefManager

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        retrofit = MyInstance.getApi
        prefManager = PrefManager(this)

        login_btn.setOnClickListener {
            if (login_password.text!!.equals("")) {
                Helper.showToast("Email required", this@LoginActivity)
                login_email.error = "Email Required"
                return@setOnClickListener
            } else if (login_password.text!!.equals("")) {
                Helper.showToast("Password required", this@LoginActivity)
                login_password.error = "Password Required"
                return@setOnClickListener
            } else {
                loginUser(login_email.text.toString(), login_password.text.toString())
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        var progressDialog: Dialog? = null
        progressDialog = Helper.showDialog(this)
        progressDialog.show()
        GlobalScope.launch(Dispatchers.IO) {
            val response = retrofit.loginUser(LoginCredentials(email, password))
            Log.d(TAG, "loginUser: ${response}")
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    prefManager.setEmail(response.body()!!.success.user.email)
                    prefManager.setToken(response.body()!!.success.token)
                    prefManager.setName(response.body()!!.success.user.name)
                    prefManager.setUid(response.body()!!.success.user.id.toString())
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).also {
                        it.addFlags(FLAG_ACTIVITY_NEW_TASK)
                        it.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                    })
                    finish()
                    finishAffinity()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Helper.showToast(response.message(), this@LoginActivity)
                }
                Log.d(TAG, "loginUser: error ${response.message()}")
            }
        }
    }
}
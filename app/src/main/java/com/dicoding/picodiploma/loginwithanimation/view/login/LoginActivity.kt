package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Intent
import android.os.*
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.picodiploma.loginwithanimation.data.user.*
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.viewmodel.ViewModelProviderFactory
import com.dicoding.picodiploma.loginwithanimation.viewmodel.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelProviderFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this.userDataStore)

        initializeView()
        configureActions()
        observeLoginState()
        observeLoadingState()
    }

    private fun initializeView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun configureActions() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launchWhenStarted {
                    loginViewModel.login(email, password)
                }
            }
        }
    }

    private fun observeLoadingState() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loadingState.collect { isLoading ->
                binding.progressBarLogIn.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        val apiResponse = it.getOrNull()
                        apiResponse?.let { response ->
                            if (!response.error!!) {
                                val loginResult = response.loginResult
                                val email = binding.edLoginEmail.text.toString()

                                val userModel = loginResult?.token?.let { token ->
                                    UserModel(
                                        email = email,
                                        token = token,
                                        isLogin = true
                                    )
                                }

                                if(userModel != null) {
                                    loginViewModel.saveSession(userModel)
                                    lifecycleScope.launch {
                                        delay(100)
                                        validateUserSession()
                                    }
                                }
                            } else {
                                displayErrorDialog(response.message)
                            }
                        }
                    } else {
                        displayErrorDialog(result.exceptionOrNull()?.message)
                    }
                }
            }
        }
    }

    private fun validateUserSession() {
        lifecycleScope.launch {
            val userModel = sessionManager.retrieveSession().first()
            if (userModel.token.isNotEmpty()) {
                if (userModel.isLogin) {
                    displaySuccessDialog(userModel.email)
                } else {
                    displayErrorDialog("User session not found.")
                }
            } else {
                displayErrorDialog("Token not found.")
            }
        }
    }

    private fun displayErrorDialog(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Login Failed!")
            .setContentText("$message")
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun displaySuccessDialog(name: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Login Successful!")
            .setContentText("Welcome, $name.")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .show()
    }
}

package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.os.*
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.viewmodel.ViewModelProviderFactory
import com.dicoding.picodiploma.loginwithanimation.viewmodel.signup.SignupViewModel

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels {
        ViewModelProviderFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
        setUpUserActions()
        observeSignupResult()
        observeLoadingState()
    }

    private fun initializeUI() {
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

    private fun setUpUserActions() {
        binding.signupButton.setOnClickListener {
            val emailInput = binding.edRegisterEmail.text.toString().trim()
            val nameInput = binding.edRegisterName.text.toString().trim()
            val passwordInput = binding.edRegisterPassword.text.toString().trim()

            if (emailInput.isEmpty() || nameInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            } else if (passwordInput.length < 8) {
                binding.edRegisterPassword.error = "Password must be at least 8 characters"
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launchWhenStarted {
                    signupViewModel.registerUser(nameInput, emailInput, passwordInput)
                }
            }
        }
    }

    private fun observeSignupResult() {
        lifecycleScope.launchWhenStarted {
            signupViewModel.signupResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        val email = binding.edRegisterEmail.text.toString()
                        showSuccessNotification(email)
                    } else {
                        showFailureNotification(it.exceptionOrNull()?.message)
                    }
                }
            }
        }
    }

    private fun observeLoadingState() {
        lifecycleScope.launchWhenStarted {
            signupViewModel.loadingState.collect { isLoading ->
                if (isLoading) {
                    binding.progressBarSignUp.visibility = View.VISIBLE
                } else {
                    binding.progressBarSignUp.visibility = View.GONE
                }
            }
        }
    }

    private fun showFailureNotification(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error!")
            .setContentText("Registration Failed: $message")
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun showSuccessNotification(email: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success!")
            .setContentText("Account for $email successfully created.")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                finish()
            }
            .show()
    }
}

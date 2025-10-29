package com.dicoding.picodiploma.loginwithanimation.data.user

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
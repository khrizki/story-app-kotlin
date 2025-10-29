package com.dicoding.picodiploma.loginwithanimation.customView

import android.content.Context
import android.text.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.loginwithanimation.R

class EmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateEmail(email: CharSequence?) {
        if (email != null && !isValidEmail(email)) {
            error = context.getString(R.string.invalid_email_format)
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return email.endsWith("@gmail.com") || email.endsWith("@bangkit.academy")
    }
}

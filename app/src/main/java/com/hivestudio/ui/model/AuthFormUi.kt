package com.hivestudio.ui.model

data class AuthFormUi(
    val email: String = "",
    val password: String = "",
    val stageName: String = "",
) {
    val canLogin: Boolean
        get() = email.isNotBlank() && password.isNotBlank()

    val canRegister: Boolean
        get() = canLogin && stageName.isNotBlank()
}

package com.codev.android

import java.io.Serializable

data class ReqSignUp(
    var co_email: String,
    var co_password: String,
    var co_nickName: String,
    var co_name: String,
    var co_gender: String,
    var co_birth: String,
    var role: String,
    var co_loginType: String
): Serializable

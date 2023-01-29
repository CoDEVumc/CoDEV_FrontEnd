package com.example.codev

import java.io.Serializable

data class ReqSignUp(
    var co_email: String,
    var co_password: String,
    var co_nickName: String,
    var co_name: String,
    var co_birth: String,
    var role: String
): Serializable {
    constructor() : this("test","test","test","test","test","test")
}

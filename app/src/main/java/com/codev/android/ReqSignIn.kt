package com.codev.android

data class ReqSignIn(
    val co_email: String,
    val co_password: String,
    val FCMToken: String
)

data class ReqGoogleSignIn(
    val co_loginType: String,
    val accessToken: String
)

data class ReqGithubSignIn(
    val co_loginType: String,
    val code: String
)
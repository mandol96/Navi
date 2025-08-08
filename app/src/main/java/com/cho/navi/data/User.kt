package com.cho.navi.data

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val email: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

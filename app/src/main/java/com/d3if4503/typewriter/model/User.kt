package com.d3if4503.typewriter.model

import com.google.firebase.database.IgnoreExtraProperties

// [START rtdb_user_class]
@IgnoreExtraProperties
data class User(
    var alamat: String? = null,
    var status: String? = null
)
// [END rtdb_user_class]


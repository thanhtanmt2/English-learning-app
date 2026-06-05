package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message") val message: String,
    @SerializedName("verified") val verified: Boolean? = null
)

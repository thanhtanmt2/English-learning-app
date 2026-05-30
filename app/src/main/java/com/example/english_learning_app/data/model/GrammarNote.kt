package com.example.english_learning_app.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

// Khuôn dữ liệu để hứng bài học Ngữ pháp từ mạng về
data class GrammarNote(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("level") val level: String,
    @SerializedName("formula") val formula: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("example") val example: String,
    @SerializedName("commonMistakes") val commonMistakes: String,
    @SerializedName("tags") val tags: List<String>? = emptyList(),
    @SerializedName("ease_factor") val easeFactor: Double? = 2.5,
    @SerializedName("interval") val interval: Int? = 0,
    @SerializedName("next_review_date") val nextReviewDate: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(level)
        parcel.writeString(formula)
        parcel.writeString(explanation)
        parcel.writeString(example)
        parcel.writeString(commonMistakes)
        parcel.writeStringList(tags)
        parcel.writeValue(easeFactor)
        parcel.writeValue(interval)
        parcel.writeString(nextReviewDate)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GrammarNote> {
        override fun createFromParcel(parcel: Parcel): GrammarNote = GrammarNote(parcel)
        override fun newArray(size: Int): Array<GrammarNote?> = arrayOfNulls(size)
    }
}

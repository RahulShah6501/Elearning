package com.example.elearning

import android.os.Parcel
import android.os.Parcelable

data class Tutorial(
    val title: String = "",
    val description: String = "",
    val id: String = "",
    val fileUrl: String = "",
    val pdfUrl: String? = null // Ensure this field name matches Firestore
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(id)
        parcel.writeString(fileUrl)
        parcel.writeString(pdfUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tutorial> {
        override fun createFromParcel(parcel: Parcel): Tutorial {
            return Tutorial(parcel)
        }

        override fun newArray(size: Int): Array<Tutorial?> {
            return arrayOfNulls(size)
        }
    }
}

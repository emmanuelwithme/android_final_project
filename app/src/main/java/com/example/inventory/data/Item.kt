package com.example.inventory.data

import android.media.Image
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// In here, create a table Entity and name it "item"

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "vocEnglish")
    val vocEnglish: String,
    @ColumnInfo(name = "vocChinese")
    val vocChinese: String,
    @ColumnInfo(name = "vocFavorite")
    val vocFavorite: Boolean,
    @ColumnInfo(name = "birthday") val birthday: Date,
    @ColumnInfo(name = "phone") val phone : String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "photo") val photo: Uri
    )
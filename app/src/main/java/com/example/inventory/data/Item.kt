package com.example.inventory.data

import android.content.ContentValues
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.inventory.MainActivity.Companion.ITEM_BIRTHDAY
import com.example.inventory.MainActivity.Companion.ITEM_CHINESE
import com.example.inventory.MainActivity.Companion.ITEM_EMAIL
import com.example.inventory.MainActivity.Companion.ITEM_ENGLISH
import com.example.inventory.MainActivity.Companion.ITEM_FAVORITE
import com.example.inventory.MainActivity.Companion.ITEM_ID
import com.example.inventory.MainActivity.Companion.ITEM_PHONE
import com.example.inventory.MainActivity.Companion.ITEM_PHOTO
import java.text.SimpleDateFormat
import java.util.*

// In here, create a table Entity and name it "item"

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "vocEnglish")
    var vocEnglish: String,
    @ColumnInfo(name = "vocChinese")
    var vocChinese: String,
    @ColumnInfo(name = "vocFavorite")
    var vocFavorite: Boolean,
    @ColumnInfo(name = "birthday")
    var birthday: Date,
    @ColumnInfo(name = "phone")
    var phone: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "photo")
    var photo: Uri
) {

    /*
    * Outside apps do not know the exact structure of your Item class,
    * so they send your ContentProvider a general object of the type ContentValues,
    * leaving it up to you to turn it into the desired object (in our case, an Item)
    * */
    companion object {
        fun fromContentValues(contentValues: ContentValues): Item {

            val dateString = "2000.02.02"
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date = formatter.parse(dateString)
            val emptyUri = Uri.EMPTY

            val item = Item(0,"item1-eng", "item1-cn", false,
                date!!, "0988222222", "qwe124@gmail.com", emptyUri)

            if (contentValues.containsKey(ITEM_ID)) {
                item.id = contentValues.getAsInteger(ITEM_ID)
            }
            if (contentValues.containsKey(ITEM_ENGLISH)) {
                item.vocEnglish = contentValues.getAsString(ITEM_ENGLISH)
            }
            if (contentValues.containsKey(ITEM_CHINESE)) {
                item.vocChinese = contentValues.getAsString(ITEM_CHINESE)
            }
            if (contentValues.containsKey(ITEM_FAVORITE)) {
                item.vocFavorite = contentValues.getAsBoolean(ITEM_FAVORITE)
            }
            if (contentValues.containsKey(ITEM_BIRTHDAY)) {
                val birthdayString = contentValues.getAsString(ITEM_BIRTHDAY)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)
                // Found: "Date?", Required: Date
                item.birthday = formatter.parse(birthdayString)!!
            }
            if (contentValues.containsKey(ITEM_PHONE)) {
                item.phone = contentValues.getAsString(ITEM_PHONE)
            }
            if (contentValues.containsKey(ITEM_EMAIL)) {
                item.email = contentValues.getAsString(ITEM_EMAIL)
            }
            if (contentValues.containsKey(ITEM_PHOTO)) {
                val stringPhoto = contentValues.getAsString(ITEM_PHOTO)
                item.photo = Uri.parse(stringPhoto)
            }
            return item
        }
    }
    /* The class ContentValues is actually a fairly basic dictionary
     * that allows us to retrieve a value from a key
     */
}

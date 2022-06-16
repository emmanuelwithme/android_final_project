package com.example.inventory.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import com.example.inventory.data.ItemRoomDatabase


class ContactsContentProvider : ContentProvider() {

    // Defines a handle to the Room database
    // private lateinit var itemRoomDatabase: ItemRoomDatabase

    // Defines a DAO to perform the database operations
    private lateinit var itemDao: ItemDao

    // Things in "companion object" are literally "static type" (全域，跨 class)
    companion object {

        /* defining Content URI */
        const val AUTHORITY = "com.example.inventory"
        const val DB_TABLE_NAME = "item"

        /* the result will be "content://com.example.inventory/item"
        *  so we are pointing at contents in table which name is "item" */
        private const val URL = "content://$AUTHORITY"
        val CONTENT_URI: Uri = Uri.parse(URL)

        /* declaring all column names and values */
        /*val ID = "_id"
        const val vocEnglish = "vocEnglish"
        const val vocChinese = "vocChinese"
        const val vocFavorite = "vocFavorite"
        const val birthday = "birthday"
        const val email = "email"
        const val phone = "phone"*/

        // for URIMatcher's return value to reference:
        // 1 is for the entire item table
        // 2 is for a id of a specific row in the item table
        const val ITEMS_TABLE = 1
        const val ITEMS_TABLE_ROW = 2
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        uriMatcher.addURI(AUTHORITY, DB_TABLE_NAME, ITEMS_TABLE)
        uriMatcher.addURI(AUTHORITY, "$DB_TABLE_NAME/#", ITEMS_TABLE_ROW)
        // equals: uriMatcher.addURI(AUTHORITY, DB_NAME + "/#", ITEMS_TABLE_ROW)
    }

    // initialize CP, don't include lengthy operations!
    override fun onCreate(): Boolean {
        Log.d("TAG", (context == null).toString() + ", onCreate")
        return true
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? =

        /* This method now needs to be modified to perform the following tasks:
        * Use the sUriMatcher object to identify the URI type.
        * Throw an exception if the URI is not valid.
        * Obtain a reference to a writable instance of the underlying SQLite database.
        * Perform a SQL insert operation to insert the data into the database table.
        * Notify the corresponding content resolver that the database has been modified.
        * Return the URI of the newly added table row.
        * */

        context?.let {
            when( uriMatcher.match(uri)) {
                ITEMS_TABLE -> {
                    val id = itemDao.insertRow(Item.fromContentValues(contentValues!!))
                    it.contentResolver.notifyChange(uri, null)
                    return@let ContentUris.withAppendedId(uri, id)
                }
                ITEMS_TABLE_ROW ->
                    throw IllegalArgumentException("Invalid Uri, can not insert with row ID: $uri")
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        }
        //throw IllegalArgumentException("Failed to insert row into $uri")

        /*context.let {
            when (uriMatcher.match(uri)) {
                ITEMS -> {
                    val id = itemDao.inserttt(Item.fromContentValues(contentValues!!))
                    it?.contentResolver?.notifyChange(uri, null)
                    return@let ContentUris.withAppendedId(uri, id)
                }
                ITEMS_ID -> throw IllegalArgumentException("Invalid Uri, can insert with ID: $uri")
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        }*/

        /*if (context != null) {

            // use the companion object defined in Item.kt
            val id: Long = ItemRoomDatabase.getDatabase(context!!).itemDao()
                .insertRow(Item.fromContentValues(contentValues!!))
            Log.d("TAG", "ContentProvider Inserted result: $id ")
            if (id != 0L) {
                context!!.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }*/


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {

        /*
        * A typical delete() method will also perform the following, tasks when called:
        * Use the sUriMatcher to identify the URI type.
        * Throw an exception if the URI is not valid.
        * Obtain a reference to a writable instance of the underlying SQLite database.
        * Perform the appropriate delete operation on the database depending on the selection criteria and the Uri type.
        * Notify the content resolver of the database change.
        * Return the number of rows deleted as a result of the operation.
        */

            if (context != null) {
                val count: Int = ItemRoomDatabase.getDatabase(context!!).itemDao()
                    .deleteRowById(ContentUris.parseId(uri))
                context!!.contentResolver.notifyChange(uri, null)
                Log.d("TAG", "ContentProvider Deleted $count rows")
                return count // this method returns numbers of rows deleted
            }

            throw IllegalArgumentException("Failed to delete row into $uri")
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {

        // The implementation should update all rows matching the selection
        // to set the columns according to the provided values map
        if (context != null) {
            val count: Int =
                ItemRoomDatabase.getDatabase(context!!).itemDao().updateee(
                    Item.fromContentValues(contentValues!!)
                )
            context!!.contentResolver.notifyChange(uri, null)
            Log.d("TAG", "ContentProvider Updated $count rows")
            return count // this method returns rows been updated
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {

        if (context != null) {
            val userId = ContentUris.parseId(uri)
            val cursor = ItemRoomDatabase.getDatabase(context!!).itemDao()
                .getItemsById(userId)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            Log.d("TAG", "ContentProvider Query result $cursor")
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {

        /* this method returns the MIME type of the data
        * for example:
        * If the MIME type represents multiple rows in "item" table,
        * its result will be (.dir): vnd.android.cursor.dir/vnd.com.example.provider.item
        * */
        /*
        * If the MIME type of represents single rows in "item" table,
        * its result will be (.item): vnd.android.cursor.item/vnd.com.example.provider.item
        * source: https://developer.android.com/guide/topics/providers/content-provider-creating#TableMIMETypes */

        // last edited code: return "vnd.android.cursor.item/" + Companion.AUTHORITY + "." + Companion.DB_NAME
        return when (uriMatcher.match(uri)) {
            // the return value will indicate multiple rows (.dir/)
            ITEMS_TABLE -> "vnd.android.cursor.dir/item"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }


    /*override fun query(
    uri: Uri,
    projection: Array<String?>?,
    selection: String?,
    selectionArgs: Array<String?>?,
    sortOrder: String?
): Cursor? {
    if (context != null) {
        val userId = ContentUris.parseId(uri)
        val cursor: Cursor = SaveMyTripDatabase.getInstance(context).itemDao()
            .getItemsWithCursor(userId)
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }
    throw IllegalArgumentException("Failed to query row for uri $uri")
}*/

    /*override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/" + Companion.AUTHORITY + "." + Companion.TABLE_NAME
    }*/

    /*override fun delete(
        uri: Uri,
        s: String?,
        strings: Array<String?>?
    ): Int {
        if (context != null) {
            val count: Int = SaveMyTripDatabase.getInstance(context).itemDao()
                .deleteItem(ContentUris.parseId(uri))
            context!!.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to delete row into $uri")
    }*/

    /*override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String?>?
    ): Int {
        if (context != null) {
            val count: Int =
                SaveMyTripDatabase.getInstance(context).itemDao().updateItem(
                    Item.fromContentValues(
                        contentValues!!
                    )
                )
            context!!.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }*/
}
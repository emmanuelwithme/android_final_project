package com.example.inventory.data

import android.database.Cursor
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// The DAO (Data Access Object), which we use it for accessing data from the database
// We give it a annotation "@Dao" to define it as a DAO

@Dao
interface ItemDao {

    // the "OnConflictStrategy.IGNORE" here, is when the inserting item
    // is the same as existing item (smae Id?), the inserting one
    // would be ignored from the system

    //原本有suspend，後來因為error拿掉，之後再處理
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    //原本有suspend，後來因為error拿掉，之後再處理
    @Update
    suspend fun update(item: Item)

    //原本有suspend，後來因為error拿掉，之後再處理
    @Delete
    suspend fun delete(item: Item)

    // the workloads in "suspend functions" are heavy,
    // so the Main Thread suspend those functions with suspend keyword
    // to IO Thread, so the Main Thread won't be stuck(origin form is stick)
    // by those heavy works

    @Query("SELECT * from item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from item ORDER BY vocEnglish ASC")
    fun getItems(): Flow<List<Item>>

    //1 is "TRUE"
    @Query("SELECT * from item WHERE vocFavorite=1")
    fun getFavoriteItems(): Flow<List<Item>>

    /* ------------------------------------------------- */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRow(item: Item): Long

    /*@Query("DELETE FROM item")
    fun deleteAll(): Int*/

    @Query("DELETE FROM item WHERE id = :id")
    fun deleteRowById(id: Long): Int

    @Update
    fun updateee(item: Item): Int

    @Query("SELECT * FROM item WHERE id = :id")
    fun getItemsById(id: Long): Cursor

}
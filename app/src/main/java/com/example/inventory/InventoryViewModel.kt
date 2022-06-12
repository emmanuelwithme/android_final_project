package com.example.inventory

import android.net.Uri
import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch
import java.util.*


// The ViewModel Class is a bridge between View and Model
// it's main tasks are providing data exchanging functionalities
// as you can see, we imported our model "Item"

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    var isFav = MutableLiveData<Boolean>().apply { value = false }
    var allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    // 
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    private fun getNewItemEntry(
        vocEnglish: String,
        vocChinese: String,
        vocFavorite: Boolean,
        birthday: Date,
        phone: String,
        email: String,
        photo: Uri
    ): Item {
        return Item(
            vocEnglish = vocEnglish,
            vocChinese = vocChinese,
            vocFavorite = vocFavorite,
            birthday = birthday,
            phone = phone,
            email = email,
            photo = photo
        )
    }


    // set if this vocab is fav or not
    fun setData(fav: Boolean) {

        allItems = if (fav) {
            itemDao.getFavoriteItems().asLiveData()
        } else {
            itemDao.getItems().asLiveData()
        }


    }

    // add a new vocabulary
    fun addNewItem(
        vocEnglish: String,
        vocChinese: String,
        birthday: Date,
        phone: String,
        email: String,
        photo: Uri
    ) {
        val newItem = getNewItemEntry(vocEnglish, vocChinese, false, birthday, phone, email, photo)
        insertItem(newItem)
    }

    // check if the entry in empty or not
    // 應該需要當輸入為空的時候跳出 toast ?
    fun isEntryValid(
        vocEnglish: String,
        vocChinese: String,
        birthday: Date,
        phone: String,
        email: String,
        photo: Uri
    ): Boolean {
//        || birthday == null || photo == null
        if (vocEnglish.isBlank() || vocChinese.isBlank() || birthday == null || phone.isBlank() || email.isBlank() || photo==null) {
            return false
        }
        return true
    }

    // 讀取特定 id 的值？如果是的話有需要嗎？
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    // using this fun while updating vocab
    // 回傳 item 物件？名字不符？
    private fun getUpdatedItemEntry(
        itemId: Int,
        vocEnglish: String,
        vocChinese: String,
        vocFavorite: Boolean,
        birthday: Date,
        phone: String,
        email: String,
        photo: Uri
    ): Item {
        return Item(
            id = itemId,
            vocEnglish = vocEnglish,
            vocChinese = vocChinese,
            vocFavorite = vocFavorite,
            birthday = birthday,
            phone = phone,
            email = email,
            photo = photo
        )
    }

    // 切換 favorite 狀態, NEVER USED?
    fun favoriteSwitch(item: Item) {
        val newItem = item.copy(vocFavorite = !item.vocFavorite)
        updateItem(newItem)
    }

    // 刪除 vocab
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    // duplicated updateItem function
    /*fun updateItem(itemId: Int, vocEnglish: String, vocChinese: String) {
        //需要注意!!問題 (可能為 null) retrieveItem(itemId).value!!.vocFavorite
        val updatedItem = getUpdatedItemEntry(itemId, vocEnglish, vocChinese, false)
        updateItem(updatedItem)
    }*/

    fun updateItem(
        itemId: Int, vocEnglish: String,
        vocChinese: String,
        vocFavorite: Boolean,
        birthday: Date,
        phone: String,
        email: String,
        photo: Uri
    ) {
        //需要注意!!問題 (可能為null) retrieveItem(itemId).value!!.vocFavorite
        val updatedItem = getUpdatedItemEntry(
            itemId,
            vocEnglish,
            vocChinese,
            vocFavorite,
            birthday,
            phone,
            email,
            photo
        )
        updateItem(updatedItem)
    }

}

// what is this for?
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
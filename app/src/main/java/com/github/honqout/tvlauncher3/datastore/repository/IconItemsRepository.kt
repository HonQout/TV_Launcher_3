package com.github.honqout.tvlauncher3.datastore.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.honqout.tvlauncher3.IconItem
import com.github.honqout.tvlauncher3.IconItems
import com.github.honqout.tvlauncher3.datastore.serializer.IconItemsSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val DATA_STORE_FILE_NAME = "icon_items.pb"

val Context.iconItemsDataStore: DataStore<IconItems> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = IconItemsSerializer
)

class IconItemsRepository @Inject constructor(context: Context) {
    companion object {
        private const val TAG: String = "IconItemsRepository"
        private const val NUM_FIXED_ACTIVITY = 5
    }

    private val dataStore = context.applicationContext.iconItemsDataStore
    val itemsFlow: Flow<List<IconItem>> = dataStore.data.map { it.itemsList }

    /**
     * Initialize icons with empty items.
     */
    suspend fun initializeIcons() {
        dataStore.updateData { currentData ->
            if (currentData.itemsCount != 5) {
                val emptyItems = List(5) { index ->
                    IconItem.newBuilder()
                        .setIndex(index)
                        .setPackageName("")
                        .setActivityName("")
                        .build()
                }
                IconItems.newBuilder()
                    .addAllItems(emptyItems)
                    .build()
            } else {
                currentData
            }
        }
    }

    /**
     * Set icon on the specified position.
     */
    suspend fun setIconByIndex(position: Int, packageName: String, activityName: String) {
        require(position in 0..<NUM_FIXED_ACTIVITY)

        dataStore.updateData { currentData ->
            val updatedList = currentData.itemsList.mapIndexed { index, item ->
                if (index == position) {
                    item.toBuilder()
                        .setPackageName(packageName)
                        .setActivityName(activityName)
                        .build()
                } else {
                    item
                }
            }
            IconItems.newBuilder().addAllItems(updatedList).build()

        }
    }

    /**
     * Reset icon on the specified position
     */
    suspend fun resetIconByIndex(position: Int) {
        require(position in 0..<NUM_FIXED_ACTIVITY)

        dataStore.updateData { currentData ->
            val updatedList = currentData.itemsList.mapIndexed { index, item ->
                if (index == position) {
                    item.toBuilder()
                        .setPackageName("")
                        .setActivityName("")
                        .build()
                } else {
                    item
                }
            }
            IconItems.newBuilder().addAllItems(updatedList).build()
        }
    }

    /**
     * Save all icons.
     */
    suspend fun saveAllIcons(icons: List<IconItem>) {
        dataStore.updateData { data ->
            data.toBuilder()
                .clearItems()
                .addAllItems(icons)
                .build()
        }
    }

    /**
     * Get all icons.
     */
    suspend fun getAllIcons(): List<IconItem> {
        return dataStore.data.first().itemsList
    }
}
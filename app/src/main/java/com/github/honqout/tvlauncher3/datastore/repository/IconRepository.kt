package com.github.honqout.tvlauncher3.datastore.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.honqout.tvlauncher3.IconItem
import com.github.honqout.tvlauncher3.IconItems
import com.github.honqout.tvlauncher3.datastore.serializer.IconItemsSerializer
import com.github.honqout.tvlauncher3.utils.ApplicationUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val DATA_STORE_FILE_NAME = "icon_items.pb"

val Context.iconDataStore: DataStore<IconItems> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = IconItemsSerializer
)

class IconRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    companion object {
        private const val TAG: String = "IconRepository"
        const val NUM_FIXED_ACTIVITY = 5
    }

    private val dataStore = context.iconDataStore
    val itemsFlow: Flow<List<IconItem>> = dataStore.data
        .map { it.itemsList }
        .catch { e ->
            Log.e(TAG, "Error reading icons.", e)
            emit(emptyList())
        }

    private fun createEmptyIcon(index: Int): IconItem = IconItem.newBuilder()
        .setIndex(index)
        .setPackageName("")
        .setActivityName("")
        .build()

    /**
     * Initialize icons with empty items.
     */
    suspend fun initializeIcons() {
        dataStore.updateData { currentData ->
            if (currentData.itemsCount != NUM_FIXED_ACTIVITY) {
                val emptyItems = List(NUM_FIXED_ACTIVITY) { index -> createEmptyIcon(index) }
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
     * Reset icons on the specified positions.
     */
    suspend fun resetIconsByIndex(positions: List<Int>) {
        dataStore.updateData { currentData ->
            val updatedList = currentData.itemsList.mapIndexed { index, item ->
                if (index in positions) {
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
     * Reset icon with the specified packageName.
     */
    suspend fun resetIconAfterPackageRemoved(packageName: String) {
        dataStore.updateData { currentData ->
            val updatedList = currentData.itemsList.map { item ->
                if (item.packageName == packageName) {
                    createEmptyIcon(item.index)
                } else {
                    item
                }
            }
            IconItems.newBuilder().clearItems().addAllItems(updatedList).build()
        }
    }

    /**
     * Reset icons with the specified `packageName` which are invalid.
     */
    suspend fun resetIconAfterPackageReplaced(context: Context, packageName: String) {
        dataStore.updateData { currentData ->
            val updatedList = currentData.itemsList.map { item ->
                if (item.packageName == packageName) {
                    if (
                        ApplicationUtils.hasLauncherActivity(
                            context,
                            item.packageName,
                            item.activityName
                        )
                    ) {
                        createEmptyIcon(item.index)
                    }
                }
                item
            }
            IconItems.newBuilder().clearItems().addAllItems(updatedList).build()
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
     * Get the icon which has been placed in the given position.
     */
    fun getIconByIndex(position: Int): Flow<IconItem> {
        return itemsFlow.map { list -> list.getOrNull(position) ?: createEmptyIcon(position) }
    }

    /**
     * Get all icons.
     */
    suspend fun getAllIcons(): List<IconItem> {
        return dataStore.data.first().itemsList
    }
}
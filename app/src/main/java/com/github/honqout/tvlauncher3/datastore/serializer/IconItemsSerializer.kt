package com.github.honqout.tvlauncher3.datastore.serializer

import android.util.Log
import androidx.datastore.core.Serializer
import com.github.honqout.tvlauncher3.IconItems
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object IconItemsSerializer : Serializer<IconItems> {
    const val TAG: String = "IconItemsSerializer"

    override val defaultValue: IconItems = IconItems.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): IconItems {
        try {
            return IconItems.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            Log.e(TAG, "Cannot read proto.", e)
            return defaultValue
        }
    }

    override suspend fun writeTo(t: IconItems, output: OutputStream) {
        t.writeTo(output)
    }
}
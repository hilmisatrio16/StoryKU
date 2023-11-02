package com.hilmisatrio.storyku.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysStory(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
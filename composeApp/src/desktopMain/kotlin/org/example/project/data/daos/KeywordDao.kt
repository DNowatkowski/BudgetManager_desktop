package org.example.project.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import org.example.project.data.entities.KeywordEntity

@Dao
interface KeywordDao {
    @Insert
    suspend fun insertKeyword(keyword: KeywordEntity)

    @Delete
    suspend fun deleteKeyword(keyword: KeywordEntity)
}
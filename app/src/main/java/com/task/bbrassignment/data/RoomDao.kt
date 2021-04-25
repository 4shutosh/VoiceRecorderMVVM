package com.task.bbrassignment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    // use flow here
    @Query("SELECT* FROM records")
    fun getAllRecords(): Flow<List<Record>>

    @Query(value = "SELECT* FROM records WHERE title = :title")
    suspend fun getRecord(title: String): Record

}
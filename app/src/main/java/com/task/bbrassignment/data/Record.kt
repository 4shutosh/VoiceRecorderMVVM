package com.task.bbrassignment.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "filePath")
    var filePath: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
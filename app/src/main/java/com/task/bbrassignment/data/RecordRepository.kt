package com.task.bbrassignment.data

import javax.inject.Inject

class RecordRepository @Inject constructor(private val recordDao: RecordDao) {

    suspend fun insertRecord(record: Record) = recordDao.insertRecord(record)

    fun fetchRecords() = recordDao.getAllRecords()

    suspend fun getRecordByTitle(title: String) = recordDao.getRecord(title)

}
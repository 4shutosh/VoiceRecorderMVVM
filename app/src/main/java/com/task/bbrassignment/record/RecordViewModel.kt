package com.task.bbrassignment.record

import android.media.MediaRecorder
import android.os.Environment
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.bbrassignment.data.Record
import com.task.bbrassignment.data.RecordRepository
import com.task.bbrassignment.utils.RecordState
import kotlinx.coroutines.launch
import java.io.IOException

class RecordViewModel @ViewModelInject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _recordState: MutableLiveData<RecordState<Record>> = MutableLiveData()

    val recordState: LiveData<RecordState<Record>>
        get() = _recordState

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var filePath: String


    fun startRecording(title: String) {
        // start recording here with the name, and the filePath
        // file path is the absolute path with the file name
        viewModelScope.launch {
            record(title)
            _recordState.value = RecordState.Recording
        }
    }

    private fun record(title: String) {
        filePath = Environment.getExternalStorageDirectory().absolutePath + "/" + title + ".3gp"

        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder.setOutputFile(filePath)
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun stopRecording(title: String) {
        // start recording here with the name, and the filePath
        // file path is the absolute path with the file name
        viewModelScope.launch {
            val record = stopRecord(title)
            _recordState.value = RecordState.Done(record)
        }
    }

    private fun stopRecord(title: String): Record {
        try {
            mediaRecorder.stop()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        // after stopping insert to db
        val record = Record(title = title, filePath = filePath)
        viewModelScope.launch {
            recordRepository.insertRecord(record)
        }

        // could have returned directly the above record for testing doing this
//        viewModelScope.launch {
//            val temp = recordRepository.getRecordByTitle(title)
//        }

        return record
    }

    fun validateName(name: String): Boolean {
        return name.isNotEmpty()
    }

}

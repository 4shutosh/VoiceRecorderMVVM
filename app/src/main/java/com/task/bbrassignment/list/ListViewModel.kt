package com.task.bbrassignment.list

import android.media.MediaPlayer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.task.bbrassignment.data.Record
import com.task.bbrassignment.data.RecordRepository
import com.task.bbrassignment.utils.RecordState
import java.io.IOException

class ListViewModel @ViewModelInject constructor(
    recordRepository: RecordRepository
) : ViewModel() {

    private lateinit var mediaPlayer: MediaPlayer

    // progress
    private val _progress: MutableLiveData<Int> = MutableLiveData()
    val progress: LiveData<Int>
        get() = _progress

    private var currentPosition: Int = -1

    // no need to make it live data
    var recordDuration: Int = 0

    // live data list
    val list: LiveData<List<Record>> = recordRepository.fetchRecords().asLiveData()

    private val _recordState: MutableLiveData<RecordState<Record>> = MutableLiveData()
    val recordState: LiveData<RecordState<Record>>
        get() = _recordState

    // playing audio in main thread, not using repository here


    fun initPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            _recordState.value = RecordState.End
        }
    }

    fun playRecord(filePath: String, position: Int) {
        // play usingMedia recorder

        currentPosition = position
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            _recordState.value = RecordState.End
        }
        try {
            initPlayer()
            mediaPlayer.setDataSource(filePath)

            mediaPlayer.prepare()

            mediaPlayer.start()

            recordDuration = mediaPlayer.duration
            _recordState.value = RecordState.Playing


        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getLiveProgress() {
        _progress.value = mediaPlayer.currentPosition
    }

    fun resumeRecord() {
        // risky !! used here
        // null pointer
        mediaPlayer.seekTo(progress.value!!)
        mediaPlayer.start()
        if (mediaPlayer.isPlaying) {
            _recordState.value = RecordState.Playing
        }
    }

    fun pauseRecord() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        _recordState.value = RecordState.Pause
    }

    fun playNext() {
        list.value?.let { listTemp ->
            if (listTemp.isNotEmpty()) {
                when (currentPosition) {
                    listTemp.size - 1 -> {
                        // end case
                        playRecord(listTemp[0].filePath, 0)

                    }
                    -1 -> {
                        // initial case
                        playRecord(listTemp[0].filePath, 0)
                    }
                    else -> {
                        // all other case
                        val next = currentPosition + 1
                        playRecord(listTemp[next].filePath, next)
                    }
                }

            }
        }
    }

    fun playRandom() {
        list.value?.let { recordList ->
            if (recordList.isNotEmpty()) {
                val max = recordList.size - 1
                val random = (0..max).random()
                playRecord(recordList[random].filePath, random)
            }
        }
    }

    fun playPrevious() {
        list.value?.let { listTemp ->
            if (listTemp.isNotEmpty()) {
                when (currentPosition) {
                    0 -> {
                        // end case
                        playRecord(listTemp[listTemp.size - 1].filePath, listTemp.size - 1)
                    }
                    -1 -> {
                        // initial case
                        playRecord(listTemp[listTemp.size - 1].filePath, listTemp.size - 1)
                    }
                    else -> {
                        // all other case
                        val next = currentPosition - 1
                        playRecord(listTemp[next].filePath, next)
                    }
                }

            }
        }
    }

    fun playAgain() {
        list.value?.let { recordList ->
            playRecord(recordList[currentPosition].filePath, currentPosition)
        }
    }

    fun getTitle() = list.value?.get(currentPosition)?.title
}
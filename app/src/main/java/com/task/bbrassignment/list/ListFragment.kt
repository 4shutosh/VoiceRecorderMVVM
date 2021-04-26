package com.task.bbrassignment.list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.bbrassignment.R
import com.task.bbrassignment.adapter.RecordListAdapter
import com.task.bbrassignment.databinding.ListFragmentBinding
import com.task.bbrassignment.utils.RecordState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*current method implemented directly passes the filePath and then uses directly in to the mediaPlayer*/

/*other method can be to pass the title, and then ask for the filePath from the repository using getRecordByTitle
* and in turn again using the  filePath to access the media file*/

/*current loop has more priority than shuffle, here the loop is only one type i.e. loop current song*/

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.list_fragment), RecyclerViewOnClickListener {

    private lateinit var listBinding: ListFragmentBinding

    private val viewModel: ListViewModel by viewModels()

    private lateinit var mainHandler: Handler

    private val TAG = "ListFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listBinding = ListFragmentBinding.bind(view)

        val recordAdapter = RecordListAdapter(this)

        viewModel.list.observe(viewLifecycleOwner, {
            recordAdapter.submitList(it)
        })

        mainHandler = Handler(Looper.getMainLooper())

        viewModel.initPlayer()

        listBinding.apply {
            val layoutM = LinearLayoutManager(requireContext())
            recyclerView.apply {
                layoutManager = layoutM
                adapter = recordAdapter
            }
            layoutPlayer.playPause.setOnClickListener {
                if (isLoopOn()) {
                    // play again without click action
                    playAgain()
                } else {
                    if (isShuffleOn()) {
                        playNextRandom()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No Track Selected/Shuffle OFF",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    listBinding.layoutPlayer.next.setOnClickListener {
                        // check for shuffle here
                        checkShuffleAndPlayNEXT()
                    }
                }
            }
        }

        // observe the seek through mediaPlayer current position
        viewModel.progress.observe(viewLifecycleOwner, { progress ->
            listBinding.layoutPlayer.seek.progress = progress
        })

        // disable client touch on seek bar
        listBinding.layoutPlayer.seek.setOnTouchListener { v, event -> true }

        // observe record play state
        viewModel.recordState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is RecordState.Playing -> {
                    // pause here
                    changeButtonIcon(true)
                    setRecordTitle()
                    listBinding.layoutPlayer.playPause.setOnClickListener {
                        viewModel.pauseRecord()
                        enableSeek(false)
                    }
                    listBinding.layoutPlayer.previous.setOnClickListener {
                        viewModel.playAgain()
                        // pass true here as we go from Playing to playing again
                        enableSeek(true)
                    }
                }
                is RecordState.Pause -> {
                    // play
                    changeButtonIcon(false)
                    listBinding.layoutPlayer.playPause.setOnClickListener {
                        viewModel.resumeRecord()
                        enableSeek(true)
                    }
                }
                is RecordState.End -> {
                    // end
                    enableSeek(false)
                    changeButtonIcon(false)
                    listBinding.layoutPlayer.seek.progress = 0

                    if (isLoopOn()) {
                        // play again without click action
                        playAgain()
                    } else {
                        listBinding.layoutPlayer.playPause.setOnClickListener {
                            if (isLoopOn()) {
                                // play again without click action
                                playAgain()
                            } else {
                                checkShuffleAndPlayNEXT()
                            }
                        }
                        // next should play, a record (random/or in sequence),
                        // but if loop is ON the new played record will loop
                        listBinding.layoutPlayer.next.setOnClickListener {
                            // check for shuffle here
                            checkShuffleAndPlayNEXT()
                        }
                        listBinding.layoutPlayer.previous.setOnClickListener {
                            // check for shuffle here
                            checkShuffleAndPlayPrevious()
                        }
                    }
                }
            }
        })
        listBinding.layoutPlayer.shuffle.setOnClickListener {
            changeButtonIconShuffle()
        }
        listBinding.layoutPlayer.loop.setOnClickListener {
            changeButtonIconLoop()
        }

        // next should play, a record (random/or in sequence),
        // but if loop is ON the new played record will loop
        listBinding.layoutPlayer.next.setOnClickListener {
            // check for shuffle here
            checkShuffleAndPlayNEXT()
        }
        listBinding.layoutPlayer.previous.setOnClickListener {
            checkShuffleAndPlayPrevious()

        }
    }

    override fun onItemClick(position: Int) {
        viewModel.list.value?.let {
            Log.d("OnCLick", "onItemClick: " + it[position].title)
            val filePath = it[position].filePath
            lifecycleScope.launch {
                playWithFilePath(filePath, position)
            }
        }
    }

    private fun playWithFilePath(filePath: String, position: Int) {
        viewModel.playRecord(filePath, position)
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
    }

    private fun playNext() {
        viewModel.playNext()
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
    }

    private fun playNextRandom() {
        viewModel.playRandom()
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
    }

    private fun playAgain() {
        viewModel.playAgain()
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
    }

    private fun playPrevious() {
        viewModel.playPrevious()
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
    }

    private fun checkShuffleAndPlayPrevious() {
        if (isShuffleOn()) {
            Log.d(TAG, "checkShuffleAndPlayPrevious: here")
            playNextRandom()
        } else {
            playPrevious()
        }
    }

    private fun checkShuffleAndPlayNEXT() {
        if (isShuffleOn()) {
            playNextRandom()
        } else {
            playNext()
        }
    }

    private fun setRecordTitle() {
        listBinding.layoutPlayer.recordLabel.text = viewModel.getTitle()
    }

    // could use Coroutine dispatcher here
    private fun enableSeek(bool: Boolean) {
        val runner = object : Runnable {
            override fun run() {
                viewModel.getLiveProgress()
                mainHandler.postDelayed(this, 1000)
            }
        }
        if (bool) {
            mainHandler.post(runner)
        } else {
            mainHandler.removeCallbacksAndMessages(null)
        }
    }

    private fun changeButtonIcon(bool: Boolean) {
        listBinding.layoutPlayer.playPause.isActivated = bool
    }

    private fun changeButtonIconShuffle() {
        listBinding.layoutPlayer.shuffle.isActivated = !listBinding.layoutPlayer.shuffle.isActivated
    }

    private fun changeButtonIconLoop() {
        listBinding.layoutPlayer.loop.isActivated = !listBinding.layoutPlayer.loop.isActivated
    }

    private fun isShuffleOn(): Boolean = listBinding.layoutPlayer.shuffle.isActivated
    private fun isLoopOn(): Boolean = listBinding.layoutPlayer.loop.isActivated
}

interface RecyclerViewOnClickListener {

    fun onItemClick(position: Int)
}
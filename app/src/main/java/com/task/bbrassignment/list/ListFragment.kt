package com.task.bbrassignment.list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.bbrassignment.R
import com.task.bbrassignment.adapter.RecordListAdapter
import com.task.bbrassignment.databinding.ListFragmentBinding
import com.task.bbrassignment.utils.RecordState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*current method implemented directly passes the filePath and then uses directly in to the mediaPlayer*/

/*other method can be to pass the title, and then ask for the filePath from the repository using getRecordByTitle
* and in turn again using the  filePath to access the media file*/

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.list_fragment), RecyclerViewOnClickListener {

    private lateinit var listBinding: ListFragmentBinding

    private val viewModel: ListViewModel by viewModels()

    private lateinit var mainHandler: Handler

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
            val layoutM = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
            layoutM.stackFromEnd = true
            recyclerView.apply {
                layoutManager = layoutM
                adapter = recordAdapter
            }
            layoutPlayer.playPause.setOnClickListener {
                playNext()
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

                    listBinding.layoutPlayer.playPause.setOnClickListener {
                        playNext()
                    }
                }
            }
        })

        listBinding.layoutPlayer.next.setOnClickListener {
            playNext()
        }
        listBinding.layoutPlayer.previous.setOnClickListener {
            playPrevious()
        }
    }

    override fun onItemClick(position: Int) {
        viewModel.list.value?.let {
            Log.d("OnCLick", "onItemClick: " + it[position].title)
            val filePath = it[position].filePath
            lifecycleScope.launch {
                play(filePath, position)
            }
        }
    }

    private fun play(filePath: String, position: Int) {
        viewModel.playRecord(filePath, position)
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
    }

    private fun playNext() {
        viewModel.playNext()
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
    }

    private fun playPrevious() {
        viewModel.playPrevious()
        listBinding.layoutPlayer.seek.max = viewModel.recordDuration
        enableSeek(true)
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
}

interface RecyclerViewOnClickListener {

    fun onItemClick(position: Int)
}
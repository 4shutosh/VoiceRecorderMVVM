package com.task.bbrassignment.record

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.task.bbrassignment.R
import com.task.bbrassignment.data.Record
import com.task.bbrassignment.databinding.FragmentRecordBinding
import com.task.bbrassignment.utils.RecordState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : Fragment(R.layout.fragment_record) {

    private val viewModel: RecordViewModel by viewModels()

    private lateinit var recordBinding: FragmentRecordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordBinding = FragmentRecordBinding.bind(view)

        subscribe()
        recordBinding.recordButton.setOnClickListener {
            val bool = viewModel.validateName(recordBinding.audioNameEt.text.toString())
            try {
                val imm: InputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    recordBinding.recordButton.windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (bool) {
                showProgress(true)
                val text = recordBinding.audioNameEt.text.toString()
                // start recording here and make the file name
                viewModel.startRecording(text)
                setText(text)
            } else {
                // set error to the text input
                recordBinding.audioNameEt.error = getString(R.string.pleaseEnterName)
                recordBinding.audioNameEt.requestFocus()
            }
        }

        recordBinding.stopButton.setOnClickListener {
            val text = recordBinding.audioNameEt.text.toString()
            viewModel.stopRecording(text)
            recordBinding.audioNameEt.isEnabled = true
        }
    }

    private fun subscribe() {
        viewModel.recordState.observe(viewLifecycleOwner, { recordState ->
            when (recordState) {
                is RecordState.Recording -> {
                    // recording make the visualizer here or a progress bar
                    // switch the buttons
                    recordBinding.audioNameEt.isEnabled = false
                    switchButtons()
                }
                is RecordState.Done<Record> -> {
                    // do the done thing
                    // switch the buttons
                    switchButtons()
                    recordBinding.audioNameEt.isEnabled = true
                    recordBinding.audioNameEt.text?.clear()
                    recordBinding.isRecordingTv.text = getString(R.string.done)
                    showProgress(false)
                    Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                }
                is RecordState.Error -> {
                    // handle the error
                    recordBinding.isRecordingTv.text = ""
                    recordBinding.textView.requestFocus()
                    showProgress(false)
                }
            }
        })
    }

    private fun switchButtons() {
        if (recordBinding.recordButton.visibility == View.VISIBLE) {
            recordBinding.recordButton.visibility = View.GONE
            recordBinding.stopButton.visibility = View.VISIBLE
            recordBinding.stopButton.requestFocus()
        } else if (recordBinding.stopButton.visibility == View.VISIBLE) {
            recordBinding.recordButton.visibility = View.VISIBLE
            recordBinding.stopButton.visibility = View.GONE
            recordBinding.recordButton.requestFocus()
        }
    }

    private fun showProgress(bool: Boolean) {
        recordBinding.progress.visibility = if (bool) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun setText(text: String) {
        val finalText: String = getString(R.string.isRecording) + " " + text
        val ss = SpannableString(finalText)
        val cyan = ForegroundColorSpan(getColor(requireActivity(), R.color.cyan))
        ss.setSpan(cyan, 25, finalText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        recordBinding.isRecordingTv.text = ss
    }

}
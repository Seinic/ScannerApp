package com.test.scannerapp.util

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.test.scannerapp.R
import com.test.scannerapp.ui.scanner.ScannerViewModel.State

class BindingAdapters {
    companion object{
        @JvmStatic
        @BindingAdapter("bind:set_header")
        fun setHeader(header: TextView, state: State){
            when(state){
                State.SUCCESS -> {
                    header.visibility = View.VISIBLE
                    header.text = header.context.getString(R.string.scan_success_header)
                    header.setTextColor(Color.GREEN)
                }
                State.ERROR -> {
                    header.visibility = View.VISIBLE
                    header.text = header.context.getString(R.string.scan_error_header)
                    header.setTextColor(Color.RED)
                }
                State.INIT -> {
                    header.visibility = View.GONE
                }
            }
        }

        @JvmStatic
        @BindingAdapter("bind:set_body", "bind:scan_result")
        fun setBody(body: TextView, state: State, scanResult: String){
            when (state){
                State.SUCCESS -> {
                    body.visibility = View.VISIBLE
                    body.text = scanResult
                }
                State.ERROR -> {
                    body.visibility = View.VISIBLE
                    body.text = body.context.getString(R.string.unreadable_barcode_message)
                    body.setTextColor(Color.BLACK)
                }
                State.INIT -> {
                    body.visibility = View.GONE
                }
            }
        }

        @JvmStatic
        @BindingAdapter("bind:is_url")
        fun setUrlTextView(textView: TextView, isUrl: Boolean){
            textView.isClickable = isUrl
            if (isUrl){
                textView.setTextColor(Color.BLUE)
            } else {
                textView.setTextColor(Color.BLACK)
            }
        }

        @JvmStatic
        @BindingAdapter("bind:on_click_with_value")
        fun setTextViewOnClickWithValue(textView: TextView, callback:OnTextViewClick){
            textView.setOnClickListener {
                callback.onClick(textView.text.toString())
            }
        }

        interface OnTextViewClick{
            fun onClick(value: String)
        }
    }
}
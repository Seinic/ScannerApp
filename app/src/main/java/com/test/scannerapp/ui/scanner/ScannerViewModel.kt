package com.test.scannerapp.ui.scanner

import android.graphics.Bitmap
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import io.reactivex.subjects.PublishSubject

class ScannerViewModel : ViewModel(), LifecycleObserver { //TODO inject detector

    val publishSubject: PublishSubject<Event> = PublishSubject.create()
    val state = ObservableField(State.INIT)
    val scanResult = ObservableField("")
    val isScanResultUrl = ObservableBoolean(false)

    fun onImageTaken(detector: BarcodeDetector, imageBitmap: Bitmap) {
        if (detector.isOperational) {
            decodeBarcode(detector, imageBitmap)
        } else {
            publishSubject.onNext(Event.UnknownErrorOccurred)
        }
    }

    private fun decodeBarcode(detector: BarcodeDetector, imageBitmap: Bitmap) {
        val frame = Frame.Builder().setBitmap(imageBitmap).build()
        val barcodes = detector.detect(frame)
        if (barcodes.size() == 0) {
            state.set(State.ERROR)
        } else {
            state.set(State.SUCCESS)
            scanResult.set(barcodes.valueAt(0).displayValue)
            when (barcodes.valueAt(0).valueFormat) {
                Barcode.URL -> isScanResultUrl.set(true)
                else -> isScanResultUrl.set(false)
            }
        }
    }

    fun requestNewScan() {
        publishSubject.onNext(Event.NewScanRequest)
    }

    fun goToUrl(url: String) {
        publishSubject.onNext(Event.NavigateToUrl(url))
    }

    enum class State {
        INIT,
        SUCCESS,
        ERROR
    }

    sealed class Event {
        object UnknownErrorOccurred : Event()
        object NewScanRequest : Event()
        data class NavigateToUrl(val url: String) : Event()
    }
}
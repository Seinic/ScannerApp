package com.test.scannerapp.ui.scanner

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.test.scannerapp.R
import com.test.scannerapp.databinding.FragmentScannerBinding
import com.test.scannerapp.ui.scanner.ScannerViewModel.Event
import com.test.scannerapp.ui.scanner.ScannerViewModel.State
import com.test.scannerapp.util.REQUEST_IMAGE_CAPTURE
import io.reactivex.disposables.CompositeDisposable


class ScannerFragment : Fragment() {

    private val viewModel: ScannerViewModel by viewModels()
    private val disposable = CompositeDisposable()
    private var fragemntRecreated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentScannerBinding.inflate(inflater, container, false)
        lifecycle.addObserver(viewModel)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null){
            fragemntRecreated = savedInstanceState.getBoolean("RECREATED", false)
        }
        if(fragemntRecreated.not()) {
            takePicture()
        }
    }

    override fun onResume() {
        super.onResume()
        handleEvents()
    }

    private fun handleEvents() {
        disposable.add(viewModel.publishSubject.subscribe {
            when (it) {
                is Event.UnknownErrorOccurred -> displayUnknownErrorMessage()
                is Event.NewScanRequest -> takePicture()
                is Event.NavigateToUrl -> navigateToUrl(it.url)
            }
        })
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(
                    takePictureIntent,
                    REQUEST_IMAGE_CAPTURE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val detector =
                BarcodeDetector.Builder(requireActivity()) //TODO build in app module and Inject in VM
                    .setBarcodeFormats(Barcode.ALL_FORMATS)
                    .build()
            viewModel.onImageTaken(detector, imageBitmap)
        } else {
            displayUnknownErrorMessage()
        }
    }

    private fun displayUnknownErrorMessage() {
        viewModel.state.set(State.INIT)
        Toast.makeText(requireContext(), getString(R.string.scan_unknown_error), Toast.LENGTH_SHORT)
            .show()
    }

    private fun navigateToUrl(url: String) {
        requireActivity().startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }

    override fun onPause() {
        disposable.clear()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("RECREATED", true)
    }
}
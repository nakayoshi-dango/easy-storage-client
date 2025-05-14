package com.example.easy_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class ScannerFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var etBarcode: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnScan: FloatingActionButton
    private var scanned = true

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)

        previewView = view.findViewById(R.id.previewView)
        etBarcode = view.findViewById(R.id.etUsername)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnScan = view.findViewById(R.id.btnScan)

        // 🔐 Pedir permiso de cámara si no está concedido
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        // ✅ Enviar resultado al fragmento anterior
        btnSubmit.setOnClickListener {
            if (!etBarcode.text.isBlank()) {
                val result = Bundle()
                result.putString("scannedProductId", etBarcode.text.toString())
                setFragmentResult("scannerResult", result)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        btnScan.setOnClickListener {
            scanned = false
        }

        return view
    }


    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val barcodeScanner = BarcodeScanning.getClient()
            val analysis = ImageAnalysis.Builder().build()
            analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image =
                        InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty() && !scanned) {
                                val value = barcodes[0].rawValue
                                etBarcode.setText(value)
                                scanned = true
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, analysis)

        }, ContextCompat.getMainExecutor(requireContext()))
    }
}

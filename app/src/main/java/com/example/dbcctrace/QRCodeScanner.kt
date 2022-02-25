package com.example.dbcctrace

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.example.dbcctrace.databinding.ActivityQrCodeScannerBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class QRCodeScanner : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeScannerBinding

    private lateinit var codeScanner: CodeScanner
    private lateinit var result: TextView
    var currentDate:String? = null




    //private lateinit var database: FirebaseDatabase
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //database = FirebaseDatabase.getInstance()
        //database = database.getReference("QRdata")





        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss ")
        currentDate = sdf.format(Date())


        result = findViewById(R.id.qrresult)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)


        }else {
            startScanning()
        }

    }

    private fun startScanning() {
        val scannerView : CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan Result: ${it.text}", Toast.LENGTH_SHORT).show()

                binding.qrresult.text = it.text


                var uniqueID = UUID.randomUUID().toString()
                val scannedqr = binding.qrresult.text.toString()

                val date = currentDate




                val qrdata = it.text.toString()

                database = FirebaseDatabase.getInstance().getReference("QRdata")

                val data = QRdata(scannedqr, uniqueID, date)
                database.child(uniqueID!!).setValue(data)
                        .addOnSuccessListener {

                            Toast.makeText(this, "Successfully Saved QR data", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to Save QR data", Toast.LENGTH_SHORT).show()

                        }


                //startActivity(Intent(this, QRcodeResult::class.java)
                  //  .putExtra("QR Result: ${it.text}", result.text.toString() ))

            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera Initialization error: ${it.message}", Toast.LENGTH_SHORT).show()

            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 123){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Camera Permission Granted", Toast.LENGTH_SHORT).show()
                startScanning()
            }else{
                Toast.makeText(this,"Camera Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized){
            codeScanner.releaseResources()
        }
        super.onPause()
    }
}
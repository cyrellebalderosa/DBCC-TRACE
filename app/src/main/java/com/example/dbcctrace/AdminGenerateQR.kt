package com.example.dbcctrace

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityAdminGenerateQrBinding

class AdminGenerateQR : AppCompatActivity() {

    private lateinit var binding: ActivityAdminGenerateQrBinding


    private lateinit var tvName: EditText
    private lateinit var tvAddress: EditText
    private lateinit var etTemp: EditText
    private lateinit var eventTV: EditText
    private lateinit var ivQRCode: ImageView
    private lateinit var btngenerate: Button
    private val STORAGE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminGenerateQrBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //init

        tvName = findViewById(R.id.adminNametv)
        tvAddress = findViewById(R.id.adminaddressTV)
        etTemp = findViewById(R.id.adminetTemp)
        eventTV = findViewById(R.id.admineventEt)
        ivQRCode = findViewById(R.id.adminivQRCode)
        btngenerate = findViewById(R.id.adminbtngenerate)



        btngenerate.setOnClickListener {

            val temp = etTemp.text.toString().trim()
            val name = tvName.text.toString().trim()
            val address = tvAddress.text.toString().trim()
            val event = eventTV.text.toString().trim()


            val encodertemp = QRGEncoder("FullName: "+ name+ "\n"+ "temperature:"  +temp  + "\n"+ "Address: "+address + "\n" + "Event: "+event , QRGContents.Type.TEXT, 800)
            binding.adminivQRCode.setImageBitmap(encodertemp.bitmap)





        }
    }



}
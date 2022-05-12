package com.example.dbcctrace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityRecordsMainPageBinding

class RecordsMainPage : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: ActivityRecordsMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userbtn.setOnClickListener {
            startActivity(Intent(this, AdminRecordsPage::class.java))
        }

        binding.qrbtn.setOnClickListener {
            startActivity(Intent(this, QRcodeResult::class.java))
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }
}

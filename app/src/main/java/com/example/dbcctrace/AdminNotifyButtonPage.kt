package com.example.dbcctrace
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityAdminNotifyButtonPageBinding

class AdminNotifyButtonPage : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: ActivityAdminNotifyButtonPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNotifyButtonPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.smsbtn.setOnClickListener {
            startActivity(Intent(this, SMSnotify::class.java))
        }
        binding.cloudbtn.setOnClickListener {
            startActivity(Intent(this, AdminCloudNotifyPage::class.java))
        }
        
    }
}

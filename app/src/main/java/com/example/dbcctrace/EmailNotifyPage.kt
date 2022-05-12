package com.example.dbcctrace

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityEmailNotifyPageBinding

class EmailNotifyPage : AppCompatActivity() {

    private lateinit var binding: ActivityEmailNotifyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailNotifyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendbtn.setOnClickListener {

            val email = binding.emailAddress.text.toString()
            val subject = binding.subject.text.toString()
            val message = binding.message.text.toString()
            val addresses = email.split(",".toRegex()).toTypedArray()
            val intent = Intent(Intent.ACTION_SENDTO).apply {

                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL,addresses)
                putExtra(Intent.EXTRA_SUBJECT,subject)
                putExtra(Intent.EXTRA_TEXT,message)

            }

            if (intent.resolveActivity(packageManager) != null){

                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this@EmailNotifyPage,"Required app is not installed", Toast.LENGTH_SHORT).show()
            }

        }
    }
}

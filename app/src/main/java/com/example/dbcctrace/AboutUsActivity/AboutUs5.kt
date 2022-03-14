package com.example.dbcctrace.AboutUsActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityAboutUs5Binding

class AboutUs5 : AppCompatActivity() {


    //viewBinding
    private lateinit var binding: ActivityAboutUs5Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUs5Binding.inflate(layoutInflater)
        setContentView(binding.root)




        binding.fblink.setOnClickListener {

            getUrlFromIntentfblink()
        }

        binding.gmaillink.setOnClickListener {


            val address = "dbcc.trace@gmail.com"
            val intent = Intent(Intent.ACTION_SENDTO).apply {

                data = Uri.parse("mailto:$address")
                putExtra(Intent.EXTRA_EMAIL,address)
                putExtra(Intent.EXTRA_SUBJECT,"Enter subject")

            }

            if (intent.resolveActivity(packageManager) != null){

                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this@AboutUs5,"Required app is not installed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.weblink.setOnClickListener {
            getUrlFromIntentweblink()
        }
    }


    fun getUrlFromIntentfblink() {
        val url = "https://www.facebook.com/dbccfamily"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun getUrlFromIntentweblink() {
        val url = "https://dbcctrace.wixsite.com/trace"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }






    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }
}
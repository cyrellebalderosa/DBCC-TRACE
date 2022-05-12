package com.example.dbcctrace.AboutUsActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityAboutUsBinding

class AboutUs : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aboutus1.setOnClickListener {
            startActivity(Intent(this, AboutUs1::class.java))
        }
        
        binding.aboutus2.setOnClickListener {
            startActivity(Intent(this, AboutUs2::class.java))
        }

        binding.aboutus3.setOnClickListener {
            startActivity(Intent(this, AboutUs3::class.java))
        }

        binding.aboutus4.setOnClickListener {
            startActivity(Intent(this, AboutUs4::class.java))
        }

        binding.aboutus5.setOnClickListener {
            startActivity(Intent(this, AboutUs5::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }
}

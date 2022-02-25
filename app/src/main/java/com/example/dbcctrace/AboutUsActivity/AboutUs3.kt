package com.example.dbcctrace.AboutUsActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.R

class AboutUs3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us3)
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }
}
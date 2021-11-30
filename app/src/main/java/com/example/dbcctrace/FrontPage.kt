package com.example.dbcctrace

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar

@Suppress("DEPRECATION")
class FrontPage : AppCompatActivity() {
    val SPLASH_SCREEN = 10000
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    private lateinit var imageview: ImageView
    private lateinit var titletext: TextView
    private lateinit var descriptiontext: TextView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_front_page)

        val actionBar: ActionBar? = supportActionBar
        actionBar!!.hide()


        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        imageview = findViewById(R.id.logo_image)
        titletext =findViewById(R.id.titletext)
        descriptiontext = findViewById(R.id.descriptiontext)

        imageview.animation = topAnimation
        titletext.animation = bottomAnimation
        descriptiontext.animation = bottomAnimation

        Handler().postDelayed({
          startActivity(Intent(this, LogInPage::class.java))
           finish()

             },
    SPLASH_SCREEN.toLong())



    }
}
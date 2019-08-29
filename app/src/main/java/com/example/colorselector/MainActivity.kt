package com.example.colorselector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress_color_selector.setOnClickListener {
            startActivity(Intent(this, ProgressColorSelectorActivity::class.java))
        }

        vertical_progress_color_selector.setOnClickListener {
            startActivity(Intent(this, VerticalProgressColorSelectorActivity::class.java))
        }
    }
}

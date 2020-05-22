package com.wrstudio.pathview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        vPath.update(arrayListOf(Pair(10.toDouble(), 50.toDouble()),
            Pair(20.toDouble(),50.toDouble()),
            Pair(60.toDouble(),51.toDouble())))
    }
}

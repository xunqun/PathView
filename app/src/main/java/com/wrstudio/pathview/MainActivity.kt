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
        vPath.update(arrayListOf(Pair(2.toDouble(),5.toDouble()),
            Pair(9.toDouble(),8.toDouble()),
            Pair(0.toDouble(),15.toDouble())))
    }
}

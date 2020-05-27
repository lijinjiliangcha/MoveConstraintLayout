package com.lijinjiliangcha.moveconstraintlayout.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv_move.setOnClickListener {
            Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show()
        }

//        tv_move_1.setOnClickListener {
//            Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show()
//        }
    }

}

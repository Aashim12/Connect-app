package com.example.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.connect.dao.Postdao
import kotlinx.android.synthetic.main.activity_post.*

class Post : AppCompatActivity() {
    private lateinit var postdao: Postdao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        postdao= Postdao()
        postbutton.setOnClickListener {
            val input = input.text.toString().trim()
            if (input.isNotEmpty()){
            postdao.addpost(input)
                finish()
            }
        }

    }
}
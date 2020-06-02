package com.sun.foodiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //To Eat List 버튼을 누르면 해당 액티비티로 연결
        button_1.setOnClickListener{startActivity<EatlistActivity>()}

        //Food Diary 버튼을 누르면 해당 액티비티로 연결
        button_2.setOnClickListener{startActivity<FooddiaryActivity>()}
    }


}

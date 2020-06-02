package com.sun.foodiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.foodiary.Class.Diary
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_fooddiary.*


class FooddiaryActivity : AppCompatActivity() {

    val d_realm = Realm.getDefaultInstance()  //Realm 객체 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fooddiary)

        val d_realmResult = d_realm.where<Diary>()
            .findAll()
            .sort("d_date", Sort.DESCENDING)  //sort() 메서드를 이용하여 날짜순으로 내림차순 정렬

        //TodoListAdapter 클래스에 할 일 목록인 realmResult 를 전달하여 어댑터 인스턴스를 생성한다.
        //this 로 context 를 넘겨줌
        val d_adapter = DiaryListAdapter(d_realmResult, this) { d_id ->
            startActivity(Intent(this@FooddiaryActivity, DiaryEditActivity::class.java).putExtra("d_id", d_id))
        }

        //데이터가 변경되면 어댑터 적용, addChangeListner 으로 데이터가 변경될 때마다 어뎁터에 알려줌
        d_realmResult.addChangeListener { _ ->
            d_adapter.notifyDataSetChanged()
        }

        recyclerView_1.layoutManager = LinearLayoutManager(this)
        recyclerView_1.adapter = d_adapter


    }

    override fun onDestroy() {
        super.onDestroy()
        d_realm.close()   //Realm 객체 해제
    }
}

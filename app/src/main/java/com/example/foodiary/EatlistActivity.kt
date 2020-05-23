package com.example.foodiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_eatlist.*
import org.jetbrains.anko.startActivity

class EatlistActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()  //Realm 객체 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eatlist)

        val realmResult = realm.where<ToEat>()
            .findAll()
            .sort("date", Sort.DESCENDING)  //sort() 메서드를 이용하여 날짜순으로 내림차순 정렬

        //TodoListAdapter 클래스에 할 일 목록인 realmResult 를 전달하여 어댑터 인스턴스를 생성한다.
        //this 로 context 를 넘겨줌
        val adapter = ToEatListAdapter(realmResult, this) { id ->
            startActivity(Intent(this@EatlistActivity, EatlistEditActivity::class.java).putExtra("id", id))
        }

        //데이터가 변경되면 어댑터 적용, addChangeListner 으로 데이터가 변경될 때마다 어뎁터에 알려줌
        realmResult.addChangeListener { _ ->
            adapter.notifyDataSetChanged()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        //+버튼 누르면 캘린더가 나오는 수정 화면으로 이동
        addFab.setOnClickListener{startActivity<EatlistEditActivity>()}
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()   //Realm 객체 해제
    }
}

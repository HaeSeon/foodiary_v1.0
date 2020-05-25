package com.example.foodiary

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.foodiary.Class.Diary
import com.example.foodiary.Class.ToEat
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_diary_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.lang.Exception
import java.time.LocalDate
import kotlin.properties.Delegates

class DiaryEditActivity : AppCompatActivity() {

    private val OPEN_GALLERY = 1;




    var realm = Realm.getDefaultInstance()  //인스턴스 얻기
    val d_calendar: LocalDate = LocalDate.now()


    private var providedToEatId by Delegates.notNull<Long>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_edit)


        providedToEatId = intent.getLongExtra("id",-1L)


        val d_id = intent.getLongExtra("d_id", -1L)
        if(d_id == -1L){
            d_insertMode()
        }
        else{
            d_updateMode(d_id)
        }

        //사진 추가 버튼을 클릭하면 갤러리를 연다.
        button.setOnClickListener { openGallery() }

    }

    //추가모드 초기화
    private fun d_insertMode(){
        d_deleteFab.visibility= View.GONE //삭제 버튼 감추기

        //완료버튼 클릭시 실행
        d_doneFab.setOnClickListener{
            d_insertTodo()
        }
    }

    //수정모드 초기화
    private fun d_updateMode(d_id: Long) {

        //id 에 해당하는 객체를 화면에 표시
        val diary = realm.where<Diary>().equalTo("d_id", d_id).findFirst()!!
        editdiary.setText(diary.d_text)


        //완료 버튼을 클릭하면 수정
        d_doneFab.setOnClickListener {
            d_updateTodo(d_id)
        }

        //삭제 버튼을 클릭하면 삭제
        d_deleteFab.setOnClickListener{
            d_deleteTodo(d_id)

        }

    }


    private fun d_insertTodo(){

        realm.beginTransaction()    //Realm 에서 데이터를 추가, 삭제 업데이트 할 때는 beginTransaction() 매서드로 트렌젝션 시작.
        //트렌젝션 : 데이터베이스의 작업단위
        //beginTransaction(), commitTransaction() 메서드 사이에 작성한 코드는 전체가 하나의 작업
        //데이터베이스에 추가, 삭제, 업데이트를 하려면 항상 트렌젝션을 시작하고 닫아야함.

        val providedToEat = realm.where<ToEat>().equalTo("id",providedToEatId).findFirst()!!
        //새로운  ToEat 타입의 Realm 객체 생성
        val d_newItem = realm.createObject<Diary>(d_nextId())    //createObject() 메서드로 새로운 Realm 객체 생성


        //값 설정
        d_newItem.d_title = providedToEat.title    //할 일
        d_newItem.d_date = providedToEat.date   //시간 설정, timeInMillis : Long 형 값으로 변환하는 메서드
        d_newItem.d_text = editdiary.text.toString()
        //d_newItem.d_src=   //음.........뭐받지,,,,,,
        //toast("${d_newItem.d_src}")




        val deleteItem = realm.where<ToEat>().equalTo("id",providedToEatId).findFirst()!!

        deleteItem.deleteFromRealm()
        realm.commitTransaction()   //트렌젝션 종료 반영



        //다이얼로그 표시
        alert ("다이어리가 추가되었습니다."){
            yesButton { finish() }  //yes 버튼을 누르면 finish()메서드를 호출해 현재 액티비티를 종료한다.
        }.show()
    }


    private fun d_updateTodo(d_id : Long) {  //id를 인자로 받음

        realm.beginTransaction()    //Realm 에서 데이터를 추가, 삭제 업데이트 할 때는 beginTransaction() 매서드로 트렌젝션 시작.
        //트렌젝션 : 데이터베이스의 작업단위
        //beginTransaction(), commitTransaction() 메서드 사이에 작성한 코드는 전체가 하나의 작업
        //데이터베이스에 추가, 삭제, 업데이트를 하려면 항상 트렌젝션을 시작하고 닫아야함.

        //새로운  ToEat 타입의 Realm 객체 생성
        val d_update = realm.where<Diary>().equalTo("d_id", d_id).findFirst()!!   //createObject() 메서드로 새로운 Realm 객체 생성

        //값 설정
        d_update.d_text = editdiary.text.toString()


        //d_update.d_src=  //음.........뭐받지,,,,,,
        //toast("${d_update.d_src}")
        realm.commitTransaction()   //트렌젝션 종료 반영

        //다이얼로그 표시
        alert ("다이어리가 수정되었습니다."){
            yesButton { finish() }  //yes 버튼을 누르면 finish()메서드를 호출해 현재 액티비티를 종료한다.
        }.show()
    }

    //다이어리 삭제
    fun d_deleteTodo(d_id : Long){
        realm.beginTransaction()
        val d_deleteItem = realm.where<Diary>().equalTo("d_id",d_id).findFirst()!!

        d_deleteItem.deleteFromRealm()    //메서드를 이용해서 삭제
        realm.commitTransaction()
        //다이얼로그 표시
        alert ("다이어리가 삭제되었습니다. "){
            yesButton { finish() }  //yes 버튼을 누르면 finish()메서드를 호출해 현재 액티비티를 종료한다.
        }.show()
    }

    //현재 가장 큰 id 값을 얻고 1을 더해주는 메서드
    private fun d_nextId(): Int {

        //ToEat 테이블의 모든 값을 얻기 위해 where<ToEat>() 메서드를 사용한다.
        val d_maxId = realm.where<Diary>().max("d_id")   //max()메서드는 현재 id중 가장 큰 값을 얻을 때 사용한다.
        if (d_maxId != null){
            return d_maxId.toInt()+1  //id값 1 증가시켜 반환
        }

        return 0
    }

    //갤러리 열기 함수
    private fun openGallery(){
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        //결과를 수신하기 위하여 startActivity 대신 startActivityForResult 를 호출
        startActivityForResult(intent,OPEN_GALLERY)
    }

    //사용자가 startActivityForResult 의 활동을 하고오면  onActivityResult 를 호출
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode== Activity.RESULT_OK){

            if(requestCode==OPEN_GALLERY){
                //toast("open")
                var currentImageUrl : Uri? = data?.data
                try {

                    //Glide 를 통해 이미지를 이미지뷰에 띄워줌
                    //val bitmap = BitmapFactory.decodeFile(currentImageUrl.toString())
                    Glide.with(this).load(currentImageUrl).into(imageView)   //이미지를 로딩하고 into()메서드로 imageView 에 표시

                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            else{
                Log.d("ActivityResult", "something wrong")
            }
        }
    }
    override fun onDestroy() {  //액티비티가 소멸되는 생성주기인 onDestroy() 에서 인스턴스 해재
        super.onDestroy()
        realm.close()   //인스턴스 해재
    }

}

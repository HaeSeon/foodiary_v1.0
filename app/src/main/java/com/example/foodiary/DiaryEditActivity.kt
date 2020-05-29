package com.example.foodiary

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.foodiary.Class.Diary
import com.example.foodiary.Class.ToEat
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_diary_edit.*
import kotlinx.android.synthetic.main.item_diary.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.db.NULL
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.lang.Exception
import java.time.LocalDate
import kotlin.properties.Delegates



class DiaryEditActivity : AppCompatActivity() {

    private val OPEN_GALLERY = 1;




    var realm = Realm.getDefaultInstance()  //인스턴스 얻기
    val d_calendar: LocalDate = LocalDate.now()

    var imageSrc : String? = null


    private var providedToEatId by Delegates.notNull<Long>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_edit)

//        if there is Id intent : from Toeat Activity : insertMode
        providedToEatId = intent.getLongExtra("id",-1L)


//        if diary's id exist : from diary : updateMode
        val d_id = intent.getLongExtra("d_id", -1L)
        if(d_id == -1L){
            if(providedToEatId!=null){
                Log.w("Mode Identifier","Insert Mode")
                d_insertMode()
            }else{
                throw error("insert mode but not provided id value")
            }
        }
        else{
            Log.w("Mode Identifier","Update Mode")
            d_updateMode(d_id)
        }

        //사진 추가 버튼을 클릭하면 갤러리를 연다.
        button.setOnClickListener { openGallery() }
    }



    //추가모드 초기화
    private fun d_insertMode(){
        d_deleteFab.visibility= View.GONE //삭제 버튼 감추기

//        init value from provided Id : request other information from realm db
        val providedToEat = realm.where<ToEat>().equalTo("id",providedToEatId).findFirst()!!




        diaryTitle.text = providedToEat.title



        //완료버튼 클릭시 실행
        d_doneFab.setOnClickListener{
            d_insertTodo()
        }
    }

    private fun d_insertTodo(){

         //Realm 에서 데이터를 추가, 삭제 업데이트 할 때는 beginTransaction() 매서드로 트렌젝션 시작.
        //트렌젝션 : 데이터베이스의 작업단위
        //beginTransaction(), commitTransaction() 메서드 사이에 작성한 코드는 전체가 하나의 작업
        //데이터베이스에 추가, 삭제, 업데이트를 하려면 항상 트렌젝션을 시작하고 닫아야함.

        realm.beginTransaction()
        val providedToEat = realm.where<ToEat>().equalTo("id",providedToEatId).findFirst()!!
        //새로운  ToEat 타입의 Realm 객체 생성
        val d_newItem = realm.createObject<Diary>(d_nextId())    //createObject() 메서드로 새로운 Realm 객체 생성


        //값 설정
        d_newItem.d_title = diaryTitle.text.toString()
        d_newItem.d_date = providedToEat.date
        d_newItem.d_text = editdiary.text.toString()
        d_newItem.d_src = imageSrc!!
        //toast("${temp}")



        val deleteItem = realm.where<ToEat>().equalTo("id",providedToEatId).findFirst()!!

        deleteItem.deleteFromRealm()
        realm.commitTransaction()   //트렌젝션 종료 반영


        //다이얼로그 표시
        alert ("다이어리가 추가되었습니다."){
            yesButton { finish() }  //yes 버튼을 누르면 finish()메서드를 호출해 현재 액티비티를 종료한다.
        }.show()
    }



    //수정모드 초기화
    private fun d_updateMode(d_id: Long) {

        //id 에 해당하는 객체를 화면에 표시
        val diary = realm.where<Diary>().equalTo("d_id", d_id).findFirst()!!
        Log.w("image src chk", diary.d_src)
        editdiary.setText(diary.d_text)
        diaryTitle.setText(diary.d_title)
        Glide.with(this).load(diary.d_src).into(imageView)
        imageSrc = diary.d_src




        //완료 버튼을 클릭하면 수정
        d_doneFab.setOnClickListener {
            d_updateTodo(d_id)
        }

        //삭제 버튼을 클릭하면 삭제
        d_deleteFab.setOnClickListener{
            d_deleteTodo(d_id)

        }

    }



    private fun d_updateTodo(d_id : Long) {  //id를 인자로 받음
        if(imageSrc==null){
            toast("please select image")
        }


        realm.beginTransaction()    //Realm 에서 데이터를 추가, 삭제 업데이트 할 때는 beginTransaction() 매서드로 트렌젝션 시작.
        //트렌젝션 : 데이터베이스의 작업단위
        //beginTransaction(), commitTransaction() 메서드 사이에 작성한 코드는 전체가 하나의 작업
        //데이터베이스에 추가, 삭제, 업데이트를 하려면 항상 트렌젝션을 시작하고 닫아야함.




        //새로운  ToEat 타입의 Realm 객체 생성
        val d_update = realm.where<Diary>().equalTo("d_id", d_id).findFirst()!!   //createObject() 메서드로 새로운 Realm 객체 생성


        //값 설정
        d_update.d_text = editdiary.text.toString()
        d_update.d_src = imageSrc!!



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
        val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
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
                imageSrc = currentImageUrl.toString()
                toast("Uri: ${currentImageUrl}")
                try {
                    //Glide 를 통해 이미지를 이미지뷰에 띄워줌
                    //val bitmap = BitmapFactory.decodeFile(currentImageUrl.toString())
                    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                    Glide.with(this).load(imageSrc).apply(requestOptions).into(findViewById<ImageView>(R.id.imageView))   //이미지를 로딩하고 into()메서드로 imageView 에 표시

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

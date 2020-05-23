package com.example.foodiary


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_eatlist_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.util.*

class EatlistEditActivity : AppCompatActivity() {

    var realm = Realm.getDefaultInstance()  //인스턴스 얻기

    val calendar: Calendar = Calendar.getInstance() //오늘 날짜로 캘린더 객체 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eatlist_edit)

        val id = intent.getLongExtra("id", -1L)
        if(id == -1L){
            insertMode()
        }
        else{
            updateMode(id)
        }

        //캘린더뷰의 날짜를 선택했을 때 Calender 객체에 설정
        //캘린더뷰에서 날짜를 선택하면 수행할 처리를 setOnDateChangeListner() 메서드로 구현
        //변경된 년, 월, 일이 year, month, dayOfMonth 로 넘어오므로 데이터베이스에 추가, 수정, 시 설정한 날짜가 반영됨
        calendarView.setOnDateChangeListener{view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }

    //추가모드 초기화
    private fun insertMode(){
        deleteFab.visibility= View.GONE //삭제 버튼 감추기

        //완료버튼 클릭시 실행
        doneFab.setOnClickListener{
            insertTodo()
        }
    }

    //수정모드 초기화
    private fun updateMode(id: Long){

        //id 에 해당하는 객체를 화면에 표시
        val toeat = realm.where<ToEat>().equalTo("id", id).findFirst()!!
        editText.setText(toeat.title)
        calendarView.date = toeat.date

        //완료 버튼을 클릭하면 수정
        doneFab.setOnClickListener{
            updateTodo(id)
        }


        //삭제 버튼을 클릭하면 삭제
        deleteFab.setOnClickListener{
            //deleteTodo(id)
        }
    }

    private fun insertTodo(){
        realm.beginTransaction()    //Realm 에서 데이터를 추가, 삭제 업데이트 할 때는 beginTransaction() 매서드로 트렌젝션 시작.
        //트렌젝션 : 데이터베이스의 작업단위
        //beginTransaction(), commitTransaction() 메서드 사이에 작성한 코드는 전체가 하나의 작업
        //데이터베이스에 추가, 삭제, 업데이트를 하려면 항상 트렌젝션을 시작하고 닫아야함.

        //새로운  ToEat 타입의 Realm 객체 생성
        val newItem = realm.createObject<ToEat>(nextId())    //createObject() 메서드로 새로운 Realm 객체 생성

        //값 설정
        newItem.title = editText.text.toString()    //할 일
        newItem.date = calendar.timeInMillis    //시간 설정, timeInMillis : Long 형 값으로 변환하는 메서드

        realm.commitTransaction()   //트렌젝션 종료 반영

        //다이얼로그 표시
        alert ("내용이 추가되었습니다."){
            yesButton { finish() }  //yes 버튼을 누르면 finish()메서드를 호출해 현재 액티비티를 종료한다.
        }.show()
    }

    //할 일 수정
    private fun updateTodo(id : Long){  //id를 인자로 받음
        realm.beginTransaction()    //트렌젝션 시작

        //Realm 객체의 <ToEat>타입 객체로부터 데이터를 얻음
        //equalTo() 메서드를 통해 조건설정. "id"칼럼에 id 값이 있다면 findFirst()메세더로 첫번째 데이터 반환
        //!!을 변수 뒤에 붙이면 null 값이 아님을 보증하게 된다.
        val updateItem = realm.where<ToEat>().equalTo("id", id).findFirst()!!

        //값 수정
        updateItem.title = editText.text.toString()    //먹을 것
        updateItem.date = calendar.timeInMillis    //시간 설정, timeInMillis : Long 형 값으로 변환하는 메서드

        realm.commitTransaction()   //트렌젝션 종료 반영

        //다이얼로그 표시
        alert ("내용이 변경되었습니다."){
            yesButton { finish() }  //yes 버튼을 누르면 finish()메서드를 호출해 현재 액티비티를 종료한다.
        }.show()


    }

    //할 일 삭제
     fun deleteTodo(id : Long){
        realm.beginTransaction()
        val deleteItem = realm.where<ToEat>().equalTo("id",id).findFirst()!!

        deleteItem.deleteFromRealm()    //메서드를 이용해서 삭제
        realm.commitTransaction()
//        //다이얼로그 표시
//        alert ("Food Diary로 이동되었습니다. "){
//            yesButton { finish() }  //yes 버튼을 누르면 finish()메서드를 호출해 현재 액티비티를 종료한다.
//        }.show()
    }


    //먹기 완료 한 것 다이어리로 이동
    fun gotoDiary(id:Long){
        realm.beginTransaction()

        val gotoItem = realm.where<ToEat>().equalTo("id",id).findFirst()!!

        //diary 로 이동을 어케시키지....realm객체를 새로 만드나...
        gotoItem.deleteFromRealm()  //목록에서 삭제


        realm.commitTransaction()   //트렌젝션 종료 반영
    }


    //현재 가장 큰 id 값을 얻고 1을 더해주는 메서드
    private fun nextId(): Int {

        //ToEat 테이블의 모든 값을 얻기 위해 where<ToEat>() 메서드를 사용한다.
        val maxId = realm.where<ToEat>().max("id")   //max()메서드는 현재 id중 가장 큰 값을 얻을 때 사용한다.
        if (maxId != null){
            return maxId.toInt()+1  //id값 1 증가시켜 반환
        }

        return 0
    }

    override fun onDestroy() {  //액티비티가 소멸되는 생성주기인 onDestroy() 에서 인스턴스 해재
        super.onDestroy()
        realm.close()   //인스턴스 해재
    }
}

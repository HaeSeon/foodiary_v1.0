package com.example.foodiary.Class

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Diary (   //Realm 객체로 만들기 위해선 모델클래스 앞에 open 을 붙이고 RealmObject 클래스를 상속받아야한다.
    @PrimaryKey var d_id : Long = 0,  //id 는 유일한 값이여야 하기 떄문에 PrimaryKey 를 주석으로 추가 (데이터를 식별하는 유일한 키 값)
    var d_title : String="",
    var d_date : Long =0,
    var d_text : String="",
    var d_src : String=""
) : RealmObject(){  //RealmObject 클래스를 상속받음

}
package com.sun.foodiary.Class

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ToEat (   //Realm 객체로 만들기 위해선 모델클래스 앞에 open 을 붙이고 RealmObject 클래스를 상속받아야한다.
    @PrimaryKey var id : Long = 0,  //id 는 유일한 값이여야 하기 떄문에 PrimaryKey 를 주석으로 추가 (데이터를 식별하는 유일한 키 값)
    var title : String="",
    var date : Long =0
) : RealmObject() {  //RealmObject 클래스를 상속받음
}
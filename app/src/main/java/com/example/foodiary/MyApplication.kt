package com.example.foodiary

import android.app.Application
import io.realm.Realm

class MyApplication : Application() {   //Application 클래스를 상속받는 myApplication 클래스를 선언
    override fun onCreate() {   //onCreate 매서드는 액티비티가 생성되기 전에 호출됨
        super.onCreate()
        Realm.init(this)    //Realm.init 메서드를 사용하여 초기화한다.
    }

}
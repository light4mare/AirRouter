package com.example.svc.honeycomb

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import route.module.Route_chatserviceimpl
import route.module.Route_loginserviceimpl
import router.air.api.AirRouter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Route_loginserviceimpl.init()
//        Route_chatserviceimpl.init()

        AirRouter.init()
        supportFragmentManager.beginTransaction().add(R.id.test_fragment, TestFragment()).commit()
//        AirRouter.post(this, "/chat/activity")
    }
}

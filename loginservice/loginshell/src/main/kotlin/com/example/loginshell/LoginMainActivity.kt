package com.example.loginshell

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.login.LoginService
import com.example.login.impl.LoginServiceImpl
import route.module.Route_loginserviceimpl
import route.module.Route_loginshell
import router.air.annotation.Route
import router.air.api.AirRouter

@Route("/login/shell")
class LoginMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

//        AirRouter.init()
//        Route_loginserviceimpl.init()
//        Route_loginshell.init()
//        ServiceLoader.put(RouteInfo("/login/shell", 0, "com.example.loginshell.LoginMainActivity"))
//        ServiceLoader.put(RouteInfo("/login/ui", 1, "com.example.login.impl.design.ui.activity.LoginActivity"))
//        ServiceLoader.put(RouteInfo("/login/service", 1, "com.example.login.impl.LoginServiceImpl"))

//        AirRouter.getCacheComponent<LoginService>("/login/service")?.let {
//            val service = it as LoginServiceImpl
//            Log.e("2333333getComponent: ", "service.hasLogin(): ${service.hasLogin()}")
//        }

//        startActivity(Intent(this, LoginActivity::class.java))
//        AirRouter.post(this, "/login/ui")
    }

    public fun initRouter(v: View) {
        AirRouter.init()
    }

    public fun goLogin(v: View) {
//        AirRouter.getCacheComponent<LoginService>("/login/service")?.let {
//            val service = it as LoginServiceImpl
//            Log.e("2333333getComponent: ", "service.hasLogin(): ${service.hasLogin()}")
//        }

        AirRouter.post(this, "/login/ui")
    }
}

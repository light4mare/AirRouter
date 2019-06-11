package com.example.chat.impl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.chatexport.ChatServiceApi
import com.example.chatserviceimpl.R
import com.example.login.LoginService
import com.example.login.LoginServiceApi
import router.air.annotation.Route
import router.air.api.AirRouter

/**
 * @author wuxi
 * @since 2019/3/6
 */
@Route(path = ChatServiceApi.CHAT_ACTIVITY, priority = 2)
class ChatActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chat)

//        AirRouter.getCacheComponent(LoginServiceApi.LOGIN_SERVICE)?.let {
//            val service = it as LoginService
//            Log.e("2333333getComponent: ", "service.hasLogin(): ${service.hasLogin()}")
//        }
        AirRouter.getService<LoginService>(LoginServiceApi.LOGIN_SERVICE)?.hasLogin()

        AirRouter.getService(LoginService::class.java)?.let {
            Log.e("LoginService", "hasLogin: ${it.hasLogin()}")
        }

        setResult(999, Intent().apply {
            putExtra("result", "ChatActivity")
        })
        finish()
    }

    fun jumpLogin() {
//        ARouter.getInstance().build("/login/LoginActivity").navigation()
    }

    fun checkLogin() {
//        val hasLogin = ARouter.getInstance().navigation(LoginService::class.java).hasLogin()
//        if (hasLogin) {
//
//        }
//
//        val loginService = ARouter.getInstance().build("/login/service").navigation() as LoginService
//        loginService.hasLogin()
    }
}
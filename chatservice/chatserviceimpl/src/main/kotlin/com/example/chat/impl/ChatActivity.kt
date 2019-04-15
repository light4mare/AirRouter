package com.example.chat.impl

import android.app.Activity
import android.os.Bundle
import com.example.chatserviceimpl.R
import router.annotation.Route

/**
 * @author wuxi
 * @since 2019/3/6
 */
@Route(path = "/chat/activity", priority = 2)
class ChatActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chat)
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
package com.example.loginshell

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.login.impl.design.ui.activity.LoginActivity
import router.annotation.Route

@Route("/login/shell")
class LoginMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        startActivity(Intent(this, LoginActivity::class.java))
    }
}

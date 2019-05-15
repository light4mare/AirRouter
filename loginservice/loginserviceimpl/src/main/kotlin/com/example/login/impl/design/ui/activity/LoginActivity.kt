package com.example.login.impl.design.ui.activity

import android.os.Bundle
import android.widget.Toast
import com.example.common.BaseActivity
import com.example.login.LoginServiceApi
import com.example.login.impl.LoginServiceImpl
import com.example.login.impl.design.viewModel.LoginViewModel
import router.air.annotation.Extra
import router.air.annotation.Route

/**
 * @author wuxi
 * @since 2019/3/5
 */
@Route(LoginServiceApi.LOGIN_ACTIVITY, 1)
class LoginActivity : BaseActivity<LoginViewModel, LoginServiceImpl>() {
    @Extra
    var account: String = ""
    @Extra(true)
    var password: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "LoginActivity", Toast.LENGTH_LONG).show()
    }
}
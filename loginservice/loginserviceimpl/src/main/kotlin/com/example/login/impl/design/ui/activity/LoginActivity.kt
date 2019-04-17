package com.example.login.impl.design.ui.activity

import android.app.Activity
import com.example.login.LoginServiceApi
import router.annotation.Extra
import router.annotation.Route

/**
 * @author wuxi
 * @since 2019/3/5
 */
@Route(LoginServiceApi.LOGIN_ACTIVITY, 1)
class LoginActivity : Activity() {
    @Extra
    var account: String = ""
    @Extra(true)
    var password: String = ""


}
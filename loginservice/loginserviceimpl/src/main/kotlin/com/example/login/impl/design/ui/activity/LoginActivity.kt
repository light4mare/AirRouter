package com.example.login.impl.design.ui.activity

import android.app.Activity
import router.annotation.Extra
import router.annotation.Route

/**
 * @author wuxi
 * @since 2019/3/5
 */
@Route("/login/ui", 1)
class LoginActivity: Activity() {
    @Extra
    var account: String = ""
    @Extra(true)
    var password: String = ""


}
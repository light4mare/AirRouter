package com.example.login.impl

import com.example.login.LoginService
import com.example.login.LoginServiceApi
import com.example.login.model.UserInfo
import router.air.annotation.Route

/**
 * @author wuxi
 * @since 2019/3/5
 */
@Route(LoginServiceApi.LOGIN_SERVICE, 1)
class LoginServiceImpl : LoginService {
    override fun hasLogin(): Boolean {
        return true
    }

    override fun login(account: String, password: String): UserInfo {
        return UserInfo()
    }

    override fun logout(): Boolean {
        return true
    }
}
package com.example.login

import com.example.login.model.UserInfo

/**
 * @author wuxi
 * @since 2019/3/5
 */
interface LoginService {
    fun hasLogin(): Boolean

    fun login(account: String, password: String): UserInfo

    fun logout(): Boolean
}
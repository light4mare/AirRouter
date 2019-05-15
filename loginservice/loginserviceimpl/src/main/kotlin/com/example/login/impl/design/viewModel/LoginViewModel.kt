package com.example.login.impl.design.viewModel

import android.arch.lifecycle.ViewModel
import router.air.annotation.Extra

/**
 * @author wuxi
 * @since 2019/5/15
 */
class LoginViewModel : ViewModel() {
    @Extra
    val orderId: String = ""
    @Extra
    val leftTime: Long = 0
    @Extra
    val hasPay: Boolean = false
}
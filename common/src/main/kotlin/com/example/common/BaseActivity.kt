package com.example.common

import android.arch.lifecycle.ViewModel
import android.support.v7.app.AppCompatActivity
import router.air.annotation.VM

/**
 * @author wuxi
 * @since 2019/5/15
 */
abstract class BaseActivity<T : ViewModel, K : Any> : AppCompatActivity() {
    @VM
    lateinit var vm: ViewModel
}
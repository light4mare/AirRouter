package com.example.common.dependence

/**
 * @author wuxi
 * @since 2019/3/5
 */
interface Initializer {
    fun name(): String

    fun dependon(): List<String>

    fun initlized(): Boolean

    fun initModule()
}
package com.example.common.dependence

import android.content.Context

/**
 * @author wuxi
 * @since 2019/3/5
 */
object InitialManager {
    private val initializedMap by lazy { mutableMapOf<String, Boolean>() }
    private val dependenceMap by lazy { mutableMapOf<String, MutableList<Initializer>>() }
    private val initializerList by lazy { mutableListOf<Initializer>() }

    fun registerInitializer(initializer: Initializer) {
        if (!initializerList.contains(initializer)) {
            /// 考虑回环校验
            initializerList.add(initializer)
        }
    }

    fun onAppCreate(context: Context) {
        checkModule(initializerList)
    }

    private fun checkModule(initializerList: List<Initializer>) {
        for (initializer in initializerList) {
            if (initializer.dependon().isNullOrEmpty()) {
                initModule(initializer)
            } else {
                var ready = true
                for (dependence in initializer.dependon()) {
                    if (initializedMap[dependence] != true) {
                        if (dependenceMap[dependence] == null) {
                            dependenceMap[dependence] = mutableListOf()
                        }
                        ready = false
                        if (dependenceMap[dependence]?.contains(initializer) != true) {
                            dependenceMap[dependence]?.add(initializer)
                        }
                    }
                }

                if (ready && !initializer.initlized()) {
                    initModule(initializer)
                }
            }
        }
    }

    private fun initModule(initializer: Initializer) {
        initializer.initModule()
        initializer.dependon().forEach {
            dependenceMap[it]?.remove(initializer)
        }
        initializedMap[initializer.name()] = true
        dependenceMap[initializer.name()]?.let {
            checkModule(it)
        }
    }
}
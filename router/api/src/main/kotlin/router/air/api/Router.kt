package router.air.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.util.Log
import router.air.api.ext.ifNull
import router.air.api.logistics.AirDrop
import router.air.api.logistics.Track


/**
 * 实际路由逻辑处理类
 * @author wuxi
 * @since 2019/3/13
 */
object Router {
    fun init() {

    }

    /**
     * 加载所有模块提供的组件路径
     */
    fun loadRouteAndService() {
        initialize(Constants.ROUTE_LOADER_CLASS, ServiceLoader.routeMap)
        initialize(Constants.SERVICE_LOADER_CLASS, ServiceLoader.serviceMap)
    }

    private fun initialize(clazz: String, map: Any) {
        try {
            val initClass = Class.forName(clazz)
            val method = initClass.getMethod("init", Map::class.java)
            method.invoke(null, map)
        } catch (e: Exception) {
            Log.e("loadRouteAndService", e.message)
        }
    }

    fun post(host: Any, airDrop: AirDrop, interceptor: Track?, requestCode: Int = -1) {
        val routeInfo = ServiceLoader.getRouteInfo(airDrop.getUri())

        routeInfo?.apply {
            interceptor?.onFound()
            val intent = Intent()
            intent.putExtras(airDrop.getExtras())

            if (host is Fragment) {
                intent.setClassName(host.requireContext(), routeInfo.classPath)
            } else if (host is Context) {
                intent.setClassName(host, routeInfo.classPath)
            }

            if (airDrop.getFlags() != -1) {
                intent.flags = airDrop.getFlags()
            } else if (host !is Activity && host !is Fragment) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            when {
                requestCode != -1 -> when (host) {
                    is Fragment -> host.startActivityForResult(intent, requestCode)
                    is Activity -> host.startActivityForResult(intent, requestCode)
                    is Context -> {
                        Log.w("AirRouter", "AirDrop has requestCode but do not start by activity or fragment")
                        host.startActivity(intent)
                    }
                    else -> throw IllegalArgumentException("host is not a context or fragment!!!")
                }
                host is Context -> host.startActivity(intent)
                host is Fragment -> host.startActivity(intent)
            }
            interceptor?.onArrival()
        }.ifNull {
            interceptor?.onLost()
        }
    }
}
package router.air.api

import android.content.Context
import router.air.annotation.ServiceLoader
import router.air.api.logistics.AirDrop

/**
 * Api封装类
 * @author wuxi
 * @since 2019/4/16
 */
@Suppress("UNCHECKED_CAST")
object AirRouter {
    private val componentCache = java.util.HashMap<String, Any>()

    fun init() {
//        Router.loadRouteAndService()
    }

    fun post(context: Context, uri: String){
        AirDrop(uri).post(context)
    }

    fun build(uri: String): AirDrop {
        return AirDrop(uri)
    }

    fun <T> getCacheComponent(uri: String): T? {
        val component = componentCache.get(uri)
        if (component != null) {
            return component as T
        }

        val instance = getComponent<T>(uri)
        if (instance != null) {
            componentCache.put(uri, instance)
        }
        return instance
    }

    fun <T> getComponent(uri: String): T? {
        val routeInfo = ServiceLoader.getRouteInfo(uri)
        if (routeInfo != null) {
            val instance = Class.forName(routeInfo.classPath).newInstance()
            return instance as T
        }
        return null
    }
}
package router.air.api

import android.content.Context
import router.air.api.logistics.AirDrop

/**
 * Api封装类
 * @author wuxi
 * @since 2019/4/16
 */
@Suppress("UNCHECKED_CAST")
object AirRouter {
    private val componentCache = mutableMapOf<String, Any>()

    fun init() {
        Router.loadRouteAndService()
    }

    fun post(context: Any, uri: String, requestCode: Int = -1) {
        AirDrop(uri).post(context, requestCode)
    }

    fun build(uri: String): AirDrop {
        return AirDrop(uri)
    }

    fun <T: Any> getCacheService(uri: String): T? {
        val component = componentCache[uri]
        if (component != null) {
            return component as T
        }

        val instance = getService<T>(uri)
        if (instance != null) {
            componentCache[uri] = instance
        }
        return instance
    }

    fun <T> getService(uri: String): T? {
        val routeInfo = ServiceLoader.getRouteInfo(uri)
        if (routeInfo != null) {
            val instance = Class.forName(routeInfo.classPath).newInstance()
            return instance as T
        }
        return null
    }

    fun <T> getService(clazz: Class<T>): T? {
        ServiceLoader.getServiceInfo(clazz.name)?.let {
            val instance = Class.forName(it.classPath).newInstance()
            return instance as T
        }

        return null
    }
}
package router.api

import android.content.Context
import android.content.Intent
import router.annotation.ServiceLoader

/**
 * @author wuxi
 * @since 2019/4/16
 */
@Suppress("UNCHECKED_CAST")
object AirRouter {
    private val componentCache = java.util.HashMap<String, Any>()

    fun init() {
//        Router.loadRouteAndService()
    }

    fun startUri(context: Context, uri: String) {
        val routeInfo = ServiceLoader.getRouteInfo(uri)
        if (routeInfo != null) {
            val intent = Intent()
            intent.setClassName(context, routeInfo.classPath)
            context.startActivity(intent)
        }
    }

    fun <T> getComponentCache(uri: String): T? {
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
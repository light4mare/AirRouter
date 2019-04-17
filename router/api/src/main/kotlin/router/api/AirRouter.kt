package router.api

import android.content.Context
import android.content.Intent
import router.annotation.ServiceLoader

/**
 * @author wuxi
 * @since 2019/4/16
 */
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

    fun getComponentCache(uri: String): Any? {
        val component = componentCache.get(uri)
        if (component != null) {
            return component
        }

        val instance = getComponent(uri)
        if (instance != null) {
            componentCache.put(uri, instance)
        }
        return instance
    }

    fun getComponent(uri: String):Any? {
        val routeInfo = ServiceLoader.getRouteInfo(uri)
        if (routeInfo != null) {
            val instance = Class.forName(routeInfo.classPath).newInstance()
            return instance
        }
        return null
    }
}
package router.api

import android.content.Context
import android.content.Intent
import android.util.Log
import router.annotation.ServiceLoader
import router.api.logistics.AirDrop
import router.api.logistics.Interceptor


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
//        val initClazz = context.packageName.plus(Constants.SERVICE_LOADER_CLASS)
        val initClazz = "com.air.router.RouteInitializer"
        try {
            Class.forName(initClazz).getMethod("init").invoke(null)
        } catch (e: Exception) {
            Log.e("loadRouteAndService", e.message)
        }
    }

    fun post(context: Context, airDrop: AirDrop, interceptor: Interceptor?) {
        val routeInfo = ServiceLoader.getRouteInfo(airDrop.getUri())
        if (routeInfo != null) {
            val intent = Intent()
            intent.setClassName(context, routeInfo.classPath)
            intent.putExtras(airDrop.getExtras())
            intent.flags = airDrop.getFlags()
            context.startActivity(intent)
        }
    }
}
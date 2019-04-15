package router.api

import android.content.Context
import android.util.Log


/**
 * @author wuxi
 * @since 2019/3/13
 */
private object Router {

    /**
     * 加载所有模块提供的组件路径
     */
    fun loadRouteAndService(context: Context) {
        val initClazz = context.packageName.plus(Constants.SERVICE_LOADER_CLASS)
        try {
            Class.forName(initClazz).getMethod("init").invoke(null)
        } catch (e: Exception) {
            Log.e("loadRouteAndService", e.message)
        }
    }
}
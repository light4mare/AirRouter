package router.annotation

import router.annotation.info.RouteInfo

/**
 * @author wuxi
 * @since 2019/3/13
 */
object ServiceLoader {
    private val routeMap by lazy { mutableMapOf<String, RouteInfo>() }

    fun put(routeInfo: RouteInfo) {
        routeMap[routeInfo.path] = routeInfo
    }
}
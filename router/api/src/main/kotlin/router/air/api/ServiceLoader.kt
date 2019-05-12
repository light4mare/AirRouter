package router.air.api

import router.air.annotation.info.RouteInfo
import router.air.annotation.info.ServiceInfo

/**
 * @author wuxi
 * @since 2019/3/13
 */
object ServiceLoader {
    val routeMap by lazy { mutableMapOf<String, RouteInfo>() }
    val serviceMap by lazy { mutableMapOf<String, ServiceInfo>() }

    fun put(routeInfo: RouteInfo) {
        routeMap[routeInfo.path] = routeInfo
    }

    fun getRouteInfo(uri: String): RouteInfo? {
        return routeMap[uri]
    }

    fun getServiceInfo(uri: String): ServiceInfo? {
        return serviceMap[uri]
    }
}
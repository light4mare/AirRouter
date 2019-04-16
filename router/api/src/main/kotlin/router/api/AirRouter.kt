package router.api

import android.content.Context

/**
 * @author wuxi
 * @since 2019/4/16
 */
object AirRouter {
    fun init() {
        Router.loadRouteAndService()
    }
}
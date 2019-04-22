package router.air.annotation.info

/**
 * ui组件信息
 * @author wuxi
 * @since 2019/3/13
 */
data class RouteInfo(
    /**
     * 路由路径
     */
    val path: String,

    /**
     * 路由优先级，可以用于降级处理
     */
    val priority: Int,

    /**
     * 完整类名
     */
    val classPath: String
)
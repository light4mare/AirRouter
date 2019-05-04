package router.air.annotation

/**
 * 路由标记，用于标记activity、service和自定义service
 * @author wuxi
 * @since 2019/3/12
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Service(
    /**
     * 路由路径
     */
    val path: String,

    /**
     * 路由优先级，可以用于降级处理
     */
    val priority: Int = 0
)
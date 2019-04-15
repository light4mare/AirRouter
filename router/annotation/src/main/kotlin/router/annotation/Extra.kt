package router.annotation

/**
 * 需要传递的参数
 * @author wuxi
 * @since 2019/3/12
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Extra(
    /**
     * 可选参数, 可以不传, 或者多参数情况下为了方便去使用链式调用
     * 声明的参数不能为private
     */
    val isOptional: Boolean = true
)
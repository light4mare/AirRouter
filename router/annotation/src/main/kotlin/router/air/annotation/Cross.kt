package router.air.annotation

import kotlin.reflect.KClass

/**
 * activity或frag的vm引用标记
 * @author wuxi
 * @since 2019/3/12
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cross(
    /**
     * Cross 类
     */
    val clazz: KClass<*>,

    /**
     * Cross 类完整路径
     */
    val path: String
)
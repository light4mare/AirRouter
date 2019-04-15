package router.compiler

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import router.annotation.Route
import java.util.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/**
 * @author wuxi
 * @since 2019/3/12
 */
@AutoService(Processor::class)
class RouterProcessor : BaseProcessor() {
    private val SEPERATOR = "_"
    private val ROUTE_ = "Route_"
    private val MODULE_NAME = "moduleName"
    private val SERVICE_LOADER_CLASS = "router.annotation.ServiceLoader"
    private val ROUTE_INFO_CLASS = "router.annotation.info.RouteInfo"

    override fun getAnnotationClass(): Class<out Annotation> {
        return Route::class.java
    }

    override fun process(set: MutableSet<out TypeElement>?, env: RoundEnvironment): Boolean {
        val sourceClassList = env.getElementsAnnotatedWith(Route::class.java)
        if (sourceClassList.size <= 0) {
            return true
        }

        printWarn("RouterProcessor process")
        val moduleName = options[MODULE_NAME] ?: "I_HAVE_NO_NAME"
        val builder = TypeSpec.classBuilder(ROUTE_.plus(moduleName).plus(Random().nextInt(1000000)))
            .addModifiers(PUBLIC, FINAL)

        val funInitSpec = MethodSpec.methodBuilder("init").addModifiers(PUBLIC, STATIC)
        val  serviceLoaderClass = className(SERVICE_LOADER_CLASS)
//        val  serviceLoaderClass = ClassName.get("router.api", "ServiceLoader")
        val routeClass = className(ROUTE_INFO_CLASS)

        for (annotatedElement in sourceClassList) {
            try {
                val annotation = annotatedElement.getAnnotation(Route::class.java)
                funInitSpec.addStatement("\$T.INSTANCE.put(new \$T(\"\$L\", \$L, \"\$L\"))", serviceLoaderClass, routeClass, annotation.path, annotation.priority, annotatedElement.asType().toString())
            } catch (e: Exception) {
                printErr(
                    annotatedElement,
                    "Could not create builder for %s: %s",
                    annotatedElement.simpleName,
                    e.message ?: ""
                )
            }

        }

        builder.addMethod(funInitSpec.build())
        brewJava("route.module", builder.build())
        return true
    }

    override fun createTypeSpec(annotatedElement: Element): TypeSpec {
        val name = ROUTE_.plus(getPathName(annotatedElement.getAnnotation(Route::class.java).path))

        val builder = TypeSpec.classBuilder(name)
            .addModifiers(PUBLIC, FINAL)

        return builder.build()
    }

    private fun getPathName(path: String): String {
        return path.replace("/", SEPERATOR)
    }

    /**
     * 从字符串获取TypeElement对象
     */
    fun typeElement(className: String): TypeElement {
        print("typeElement: $className")
        return elementUtils.getTypeElement(className)
    }

    /**
     * 从字符串获取TypeMirror对象
     */
    fun typeMirror(className: String): TypeMirror {
        return typeElement(className).asType()
    }

    /**
     * 从字符串获取ClassName对象
     */
    fun className(className: String): ClassName {
        return ClassName.get(typeElement(className))
    }
}
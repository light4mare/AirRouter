package router.air.compiler

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import router.air.annotation.Service
import java.util.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier.*
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/**
 * @author wuxi
 * @since 2019/3/12
 */
@AutoService(Processor::class)
class ServiceProcessor : BaseProcessor() {
    private val SEPERATOR = "_"
    private val ROUTE_ = "RouteService_"
    private val MODULE_NAME = "moduleName"
    private val SERVICE_INFO_CLASS = "router.air.annotation.info.ServiceInfo"
    private val routeInfoMapClass by lazy { ClassName.get("java.util", "Map") }
    private val routeInfoMapParams = "routeMap"

    override fun getAnnotationClass(): Class<out Annotation> {
        return Service::class.java
    }

    override fun process(set: MutableSet<out TypeElement>?, env: RoundEnvironment): Boolean {
        val sourceClassList = env.getElementsAnnotatedWith(Service::class.java)
        if (sourceClassList.size <= 0) {
            return true
        }

        val moduleName = options[MODULE_NAME] ?: "I_HAVE_NO_NAME".plus(Random().nextInt(1000000))
        val builder = TypeSpec.classBuilder(ROUTE_.plus(moduleName))
            .addModifiers(PUBLIC, FINAL)

        val serviceClass = className(SERVICE_INFO_CLASS)
        val stringClass = ClassName.get(String::class.java)

        val serviceInfoClass = ParameterizedTypeName.get(routeInfoMapClass, stringClass, serviceClass)
        val parameterSpec = ParameterSpec.builder(serviceInfoClass, routeInfoMapParams).build()

        val funInitSpec = MethodSpec.methodBuilder("init")
            .addModifiers(PUBLIC, STATIC)
            .addParameter(parameterSpec)

        for (annotatedElement in sourceClassList) {
            try {
                val annotation = annotatedElement.getAnnotation(Service::class.java)

                val typeElement = annotatedElement as TypeElement
                val curServiceClass = annotatedElement.asType().toString()
                for (serviceInterface in typeElement.interfaces) {
                    val interfaceClass = serviceInterface.toString()
                    funInitSpec.addStatement("\$L.put(\"\$L\", new \$T(\"\$L\", \"\$L\", \$L, \"\$L\"))",
                        routeInfoMapParams, interfaceClass, serviceClass, interfaceClass, annotation.path, annotation.priority, curServiceClass
                    )
                }

                funInitSpec.addStatement("\$L.put(\"\$L\", new \$T(\"\$L\", \"\$L\", \$L, \"\$L\"))",
                    routeInfoMapParams, annotation.path, serviceClass, curServiceClass, annotation.path, annotation.priority, curServiceClass
                )
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
        val name = ROUTE_.plus(getPathName(annotatedElement.getAnnotation(Service::class.java).path))

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
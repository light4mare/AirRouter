package router.air.compiler

import com.google.common.collect.ImmutableSet
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * @author wuxi
 * @since 2019/3/12
 */
abstract class BaseProcessor : AbstractProcessor() {
    protected lateinit var mTypes: Types
    protected lateinit var filer: Filer
    protected lateinit var messager: Messager
    protected lateinit var elementUtils: Elements
    protected lateinit var options: Map<String, String>

    protected val notNullAnnotation by lazy { ClassName.get("android.support.annotation", "NonNull") }

    override fun init(env: ProcessingEnvironment) {
        super.init(env)
        filer = env.filer
        mTypes = env.typeUtils
        elementUtils = env.elementUtils
        messager = env.messager
        options = env.options
    }

//    override fun process(set: MutableSet<out TypeElement>?, env: RoundEnvironment): Boolean {
//        for (annotatedElement in env.getElementsAnnotatedWith(getAnnotationClass())) {
//            try {
//                val spec = createTypeSpec(annotatedElement)
//                brewJava(getPackageName(annotatedElement), spec)
//            } catch (e: Exception) {
//                printErr(
//                    annotatedElement,
//                    "Could not create builder for %s: %s",
//                    annotatedElement.simpleName,
//                    e.message ?: ""
//                )
//            }
//
//        }
//        return false
//    }

    protected abstract fun getAnnotationClass(): Class<out Annotation>

    protected abstract fun createTypeSpec(annotatedElement: Element): TypeSpec

    protected fun getPackageName(e: Element): String {
        var element = e
        while (element !is PackageElement) {
            element = element.enclosingElement
        }
        return element.qualifiedName.toString()
    }

    protected fun print(message: String) {
        messager.printMessage(Diagnostic.Kind.NOTE, message)
    }

    protected fun printWarn(message: String) {
        messager.printMessage(Diagnostic.Kind.WARNING, message)
    }

    protected fun printErr(message: String) {
        messager.printMessage(Diagnostic.Kind.ERROR, message)
    }

    protected fun printErr(e: Element, msg: String, vararg args: Any) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, *args), e)
    }

//    @Throws(IOException::class)
    protected fun brewJava(packageName: String, typeSpec: TypeSpec) {
        print("brewJava")
        val builder = JavaFile.builder(packageName, typeSpec)
        val builderFile = builder.build()
        builderFile.writeTo(filer)
    }

    protected fun createNonNullParam(className: ClassName, name: String): ParameterSpec {
        val builder = ParameterSpec.builder(className, name)
        builder.addAnnotation(notNullAnnotation)
        return builder.build()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return ImmutableSet.of(getAnnotationClass().canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }
}
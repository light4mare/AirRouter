package router.air

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin

class AirRouterPlugin implements Plugin<Project> {
    private String COMPILER = ":router:compiler"
    private String ANNOTATION = ":router:annotation"
    private String MODULE_NAME = "moduleName"

    @Override
    void apply(Project project) {
        // 自动添加kapt
        def hasKapt = project.plugins.hasPlugin(Kapt3GradleSubplugin)
        if (!hasKapt) {
            project.plugins.apply(Kapt3GradleSubplugin)
        }

//        project.extensions.getByName('kapt')
        def isApp = project.plugins.hasPlugin(AppPlugin)

        // 自动添加依赖
//        if (!isApp) {
            project.dependencies {
                kapt project.project(COMPILER)
//            implementation ANNOTATION
            }
//        }

        project.afterEvaluate {
            project.dependencies {
                implementation project.project(ANNOTATION)
            }
        }

        def kaptExtension = project.extensions.getByName('kapt')
        if (kaptExtension != null) {
            kaptExtension.arguments {
                arg(MODULE_NAME, project.getName())
            }
        }

        // 只有作为启动程序module才合并代码
        // 也可以传进去决定scope
        if (isApp) {
//            project.extensions.findByType(BaseExtension.class).registerTransform(new AirTransform(isApp))
            project.extensions.findByType(AppExtension.class).registerTransform(new AirTransform(isApp))
        }

//        def androidExtension = project.extensions.getByName("android")
    }
}
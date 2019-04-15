package com.svc.air

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet

import java.util.jar.JarEntry
import java.util.jar.JarFile

class AirTransform extends Transform {
    private boolean isApp

    AirTransform(boolean isApp) {
        this.isApp = isApp
    }

    @Override
    String getName() {
        return "AirRouterTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
//        return ImmutableSet.of(TransformManager.CLASSES, TransformManager.RESOURCES)
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT
        if (isApp) {
            return TransformManager.SCOPE_FULL_PROJECT
        } else {
            return ImmutableSet.of(QualifiedContent.Scope.PROJECT)
        }
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println "----------------2333333333333333 AirTransform begin----------------"

        def outDir = transformInvocation.outputProvider.getContentLocation("route", outputTypes, scopes, Format.DIRECTORY)
        outDir.deleteDir()
        outDir.mkdirs()

        String dir = outDir.toString();
        println("routePath :" + outDir)

        transformInvocation.inputs.each {
            it.directoryInputs.each { input ->
                int length = input.file.toString().length()
                input.file.traverse { file ->
                    String className = file.toString().substring(length);
                    println("directoryInputs" + className)
//                    if (it.isDirectory()) {
////                        println "directory: " + dir + className
//                        new File(dir + className).mkdirs();
//                    } else {
//
//                    }
                }
            }

            it.jarInputs.each { input ->
                JarFile jarFile = new JarFile(input.file)
                println("jarFile :" + input.file)
                jarFile.entries().each { entry ->
                    println ("jarInputs" + entry.getName())
                }
            }
        }
    }

    private void generateRegister() {

    }
}
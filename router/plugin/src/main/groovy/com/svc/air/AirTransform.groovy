package com.svc.air

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet

import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

class AirTransform extends Transform {
    private static final ROUTE_PREFIX = "route/module/Route_"

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

        Set<String> initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

        transformInvocation.inputs.each {
            it.directoryInputs.each { input ->
                println("directoryInputs:__" + input.name)
                int length = input.file.toString().length()
                input.file.traverse { file ->
                    String className = file.toString().substring(length);
                    println("directoryInputs:__" + className)
                    if (isTargetClass(className)) {
                        initClasses.add(className)
                    }

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
//                    println ("jarInputs:__" + entry.getName())
                    String className = entry.getName()
                    if (isTargetClass(className)) {
                        initClasses.add(className)
                    }
                }
            }

            initClasses.each {
                println("initClasses: " + it)
            }
        }
    }

    private void generateRegister() {

    }

    private boolean isTargetClass(String className) {
        return className.startsWith(ROUTE_PREFIX) && className.endsWith(".class")
    }
}
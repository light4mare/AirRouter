package com.svc.air

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

class AirTransform extends Transform {
    private static final ROUTE_PREFIX = "route.module.Route_"

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
//                println("directoryInputs:__" + input.name)
                int length = input.file.toString().length()
                input.file.traverse { file ->
                    String className = file.toString().substring(length + 1).replace("\\", ".")
//                    println("directoryInputs:__" + className)
                    if (isTargetClass(className)) {
                        initClasses.add(className)
                    }
                }
            }

            it.jarInputs.each { input ->
                JarFile jarFile = new JarFile(input.file)
//                println("jarFile :" + input.file)
                jarFile.entries().each { entry ->
//                    println ("jarInputs:__" + entry.getName())
                    String className = entry.getName().replace("/", ".")
                    if (isTargetClass(className)) {
                        initClasses.add(className)
                    }
                }
            }

        }

        generateRegister(dir, initClasses)

        println "----------------2333333333333333 AirTransform finish----------------"
    }

    private void generateRegister(String directory, Set<String> classes) {
        println("initClasses.size: " + classes.size())

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM6, classWriter) {}
        String loaderClassName = "com/air/router/RouteInitializer"
        // 生成类
        classVisitor.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, loaderClassName, null, "java/lang/Object", null)
        // 生成初始化方法
        MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "init", "()V", null, null);
        // 开始写方法代码
        methodVisitor.visitCode()
        classes.each { clazz ->
            println("initClasses: " + clazz)
            String clazzName = clazz.substring(0, clazz.length() - 6)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, clazzName, "init", "()V", false);
        }
        // 由于前面用了COMPUTE_FRAMES，这里随便传
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitEnd();
        classVisitor.visitEnd();

        File pathDir = new File(directory + File.separator + "com/air/router/")
        pathDir.mkdirs()
        File transFile = new File(directory + File.separator + loaderClassName + ".class")
        transFile.bytes = classWriter.toByteArray();
    }

    private static boolean isTargetClass(String className) {
        return className.startsWith(ROUTE_PREFIX) && className.endsWith(".class")
    }
}
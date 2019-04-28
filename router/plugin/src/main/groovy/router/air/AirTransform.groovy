package router.air

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
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
        return "AirRouter"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
//        return ImmutableSet.of(TransformManager.CLASSES, TransformManager.RESOURCES)
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
//        if (isApp) {
//            return TransformManager.SCOPE_FULL_PROJECT
//        } else {
//            return ImmutableSet.of(QualifiedContent.Scope.PROJECT)
//        }
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//        super.transform(transformInvocation)

        Set<String> initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

        transformInvocation.inputs.each {
            it.directoryInputs.parallelStream().each { directoryInput ->
                    int length = directoryInput.file.toString().length()
                    directoryInput.file.traverse { file ->
                        if (file.name.contains("RoustServiceLoader")) {
                            visitOrigin(file)
                        }
                        String className = file.toString().substring(length + 1).replace("\\", ".")
                        if (isTargetClass(className)) {
                            initClasses.add(className)
                        }
                    }
                    File dst = transformInvocation.getOutputProvider().getContentLocation(
                            directoryInput.getName(), directoryInput.getContentTypes(),
                            directoryInput.getScopes(), Format.DIRECTORY);
                    FileUtils.copyDirectory(directoryInput.file, dst);
            }

//            input.getDirectoryInputs().parallelStream().forEach(directoryInput -> {
//                File src = directoryInput.getFile();
//                File dst = invocation.getOutputProvider().getContentLocation(
//                        directoryInput.getName(), directoryInput.getContentTypes(),
//                        directoryInput.getScopes(), Format.DIRECTORY);
//                try {
//                    scanDir(src, initClasses);
//                    FileUtils.copyDirectory(src, dst);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });

//            it.directoryInputs.each {
//            }

            it.jarInputs.parallelStream().each { jarInput ->
                JarFile jarFile = new JarFile(jarInput.file)
                jarFile.entries().each { entry ->
                    String className = entry.getName().replace("/", ".")
                    if (isTargetClass(className)) {
                        initClasses.add(className)
                    }
                }
                File dst = transformInvocation.getOutputProvider().getContentLocation(
                        jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
                        Format.JAR);
                FileUtils.copyFile(jarInput.file, dst)
            }

//            it.jarInputs.each { jarInput ->
//            }

//            input.getJarInputs().parallelStream().forEach(jarInput -> {
//                File src = jarInput.getFile();
//                File dst = invocation.getOutputProvider().getContentLocation(
//                        jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
//                        Format.JAR);
//                try {
//                    scanJarFile(src, initClasses);
//                    FileUtils.copyFile(src, dst);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });

        }

        File dest = transformInvocation.getOutputProvider().getContentLocation(
                "AirRouter", TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);

        String dir = dest.absolutePath

        generateRegister(dir, initClasses)
    }

    private static void generateRegister(String directory, Set<String> classes) {
        if (classes.isEmpty()) {
            return
        }

        println("initClasses.size: " + classes.size())

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, classWriter) {}
        String loaderClassName = "com/air/router/RouteInitializer"
        // 生成类
        classVisitor.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, loaderClassName, null, "java/lang/Object", null)
        // 生成初始化方法
//        "java.lang.String, router.air.annotation.info.RouteInfo"
        MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "init", "(Ljava/util/Map<>;)V", "(Ljava/util/Map<Ljava/lang/String;Lrouter/air/annotation/info/RouteInfo;>;)V", null);
        // 开始写方法代码
        methodVisitor.visitCode()
        classes.each { clazz ->
            String clazzName = clazz.substring(0, clazz.length() - 6).replace(".", "/")
            println("initClasses: " + clazzName)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, clazzName, "init", "()V", false);
        }
        // 由于前面用了COMPUTE_FRAMES，这里随便传
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitEnd();
        classVisitor.visitEnd();

//        File pathDir = new File(directory + File.separator + "com/air/router/")
//        pathDir.mkdirs()

        File transFile = new File(directory + File.separator + loaderClassName + ".class")
        transFile.getParentFile().mkdirs()
        transFile.bytes = classWriter.toByteArray();
    }

    private static boolean isTargetClass(String className) {
        return className.startsWith(ROUTE_PREFIX) && className.endsWith(".class")
    }

    private static visitOrigin(File file) {
        ClassReader classReader = new ClassReader(file.bytes);

        OriginClassVisitor visitor = new OriginClassVisitor(Opcodes.ASM7)

        classReader.accept(visitor, Opcodes.ASM7);
    }
}
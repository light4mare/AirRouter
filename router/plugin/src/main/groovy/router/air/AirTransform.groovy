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
    private static final ROUTE_SERVICE_PREFIX = "route.module.RouteService_"

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
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        Set<String> initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Set<String> initServiceClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

        transformInvocation.inputs.each {
            it.directoryInputs.each {directoryInput ->
                int length = directoryInput.file.toString().length()
                directoryInput.file.traverse { file ->
                    if (file.name.contains("RoustServiceLoader")) {
                        visitOrigin(file)
                    }
                    String className = file.toString().substring(length + 1).replace("\\", ".")
                    if (isTargetClass(className, ROUTE_PREFIX)) {
                        initClasses.add(className)
                    }
                    if (isTargetClass(className, ROUTE_SERVICE_PREFIX)) {
                        initServiceClasses.add(className)
                    }
                }
                File dst = transformInvocation.getOutputProvider().getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(), Format.DIRECTORY);
                FileUtils.copyDirectory(directoryInput.file, dst);
            }


            it.jarInputs.each { jarInput ->
                JarFile jarFile = new JarFile(jarInput.file)
                jarFile.entries().each { entry ->
                    String className = entry.getName().replace("/", ".")
                    if (isTargetClass(className, ROUTE_PREFIX)) {
                        initClasses.add(className)
                    }
                    if (isTargetClass(className, ROUTE_SERVICE_PREFIX)) {
                        initServiceClasses.add(className)
                    }
                }
                File dst = transformInvocation.getOutputProvider().getContentLocation(
                        jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
                        Format.JAR);
                FileUtils.copyFile(jarInput.file, dst)
            }
        }

        File dest = transformInvocation.getOutputProvider().getContentLocation(
                "AirRouter", TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);

        String dir = dest.absolutePath

//        generateRegister(dir, initClasses)
        generateRegister(dir, initClasses, Constants.TYPE_CLASS_ROUTE_INFO, Constants.TYPE_LOADER)
        generateRegister(dir, initServiceClasses, Constants.TYPE_CLASS_Service_INFO, Constants.TYPE_SERVICE_LOADER)
    }

    private static void generateRegister(String directory, Set<String> classes, String modelClass, String finalClass) {
        if (classes.isEmpty()) {
            return
        }

        println("initClasses.size: " + classes.size())

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {}
        String loaderClassName = finalClass
        // 生成类
        classVisitor.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, loaderClassName, null, Constants.TYPE_OBJECT, null)
        // 生成初始化方法
        //(Ljava/util/Map<Ljava/lang/String;Lrouter/air/annotation/info/RouteInfo;>;)V
        MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "init",
                "(${Constants.TYPE_CLASS_MAP})V",
                "(${Constants.TYPE_CLASS_MAP_GENERIC}<${Constants.TYPE_CLASS_STRING}${modelClass}>;)V", null);
        // 开始写方法代码
        methodVisitor.visitCode()
        classes.each { clazz ->
            String clazzName = clazz.substring(0, clazz.length() - 6).replace(".", "/")
            println("initClasses: " + clazzName)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, clazzName, "init", "($Constants.TYPE_CLASS_MAP)V", false);
        }
        // 由于前面用了COMPUTE_FRAMES，这里随便传
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitEnd();
        classVisitor.visitEnd();

        File transFile = new File(directory + File.separator + loaderClassName + Constants.DOT_CLASS)
        transFile.getParentFile().mkdirs()
        transFile.bytes = classWriter.toByteArray();
    }

    private static boolean isTargetClass(String className, String target) {
        return className.startsWith(target) && className.endsWith(Constants.DOT_CLASS)
    }

    private static visitOrigin(File file) {
        ClassReader classReader = new ClassReader(file.bytes);

        OriginClassVisitor visitor = new OriginClassVisitor(Opcodes.ASM7)

        classReader.accept(visitor, Opcodes.ASM7);
    }
}
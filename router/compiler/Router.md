## Router
### 原理
通过Java提供的Annotation Processor收集当前模块需要对外提供的功能，在内存中进行整合，用时使用相应key寻找对应功能的实现类进行调用或者跳转

#### 收集方法
1. 生成asset文件<br/>
根据当前module的名字在asset目录下生成一个名为Route_module_name的txt空文本，同时使用apt生成与此命名规则相关的**Route_module_name_loader.java(kt)**，**loader**类中将route路径注册进Router  
运行时扫描asset目录下所有文件，用命名规则匹配得出loader类名，然后进行反射调用，完成route注册
2. 直接生成Loader<br/>
apt生成Loader时生成到指定package下，运行时从这个目录下使用packageManager遍历得出所有Loader，然后反射调用
3. class级整合<br/>
apt生成Loader时生成到指定package下，使用Gradle Plugin在打包时按照命名规则扫描所有class，匹配出Loader，整合至固定命名

### Gradle Plugin
gradle文件中类似
```gradle
apply plugin: plugs.java
```
相关组件支持sdk由Gradle提供
```gradle
// 基础支持功能
implementation gradleApi()
implementation 'com.android.tools.build:gradle:3.2.1'

// kotlin plugin功能
implementation 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21'
```
Gradle Plugin 理论上可以用任何适配jvm的语言进行编写，用Java，kotlin，groovy都是可以的，但是个人比较推荐groovy，
可以使用gradle文件中的写法，同时也不会检查相关字段是否存在，当然这个跟gradleApi()有关
```groovy
class AirRouterPlugin implements Plugin<Project>{
    @Override
    void apply(Project project) {
        // 可以检查是否已使用某个plugin
        def hasKapt = project.plugins.hasPlugin(Kapt3GradleSubplugin)
        if (!hasKapt) {
            // 可以为module添加plugin
            project.plugins.apply(Kapt3GradleSubplugin)
        }
        
        // 可以检查是否是app module
        def isApp = project.plugins.hasPlugin(AppPlugin)
        if (isApp) {
        // 使用transform进行classes扫描转换
            project.extensions.findByType(AppExtension.class).registerTransform(new AirTransform(isApp))
        }
    }
}
```
完成plugin代码后需要提供jar包给外部引用，需要在  
resource.META-INF.gradle-plugins.xxxxxx.properties中注册,这个文件目前还没有什么办法生成
```text
implementation-class=router.air.AirRouterPlugin
```
本地调试方法为：在侧边栏找到这个任务后运行即可打出jar包
```gradle
group = "com.svc.air"
version = "1.0.0"

uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: 'file:../../air-route')
        }
    }
}
```
gradle plugin的执行时间在apt之后，具体使用代码：
```groovy
class AirTransform extends Transform {
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        // 扫描所有来源所提供的Route Loader
        // 使用生成ASM/cglib或Javassist 直接生成ServiceLoader.class
    }
}
```

##### 注意点：
使用transform时要手动将相关扫描文件复制到生成目录，不然这些classes就丢失了,生成文件会在build/intermidiates/transforms下，通常是最后一个编号文件夹

### ASM
&ensp;&ensp;&ensp;&ensp;ASM 是一个 Java 字节码操控框架。它能被用来动态生成类或者增强既有类的功能。ASM 可以直接产生二进制 class 文件，也可以在类被加载入 Java 虚拟机之前动态改变类行为。
Java class 被存储在严格格式定义的 .class 文件里，这些类文件拥有足够的元数据来解析类中的所有元素：类名称、方法、属性以及 Java 字节码（指令）。
ASM 从类文件中读入信息后，能够改变类行为，分析类信息，甚至能够根据用户要求生成新类。<br/>

&ensp;&ensp;&ensp;&ensp;提供的功能主要是通过visitXXXX来实现，包括所有Java元素如field, annotation, method, inner, outer等，可以用这些方法访问或者创造相关元素  
同时可以指定生成class的Java版本
```groovy
private void generateRegister(String directory, Set<String> classes, String modelClass, String finalClass){
    // 这两行基本是固定写法，自动计算生成方法和类的栈帧
    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
    ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {}
    
    // 生成初始化方法
    //(Ljava/util/Map<Ljava/lang/String;Lrouter/air/annotation/info/RouteInfo;>;)V
    MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            "init",
            "(${Constants.TYPE_CLASS_MAP})V",
            "(${Constants.TYPE_CLASS_MAP_GENERIC}<${Constants.TYPE_CLASS_STRING}${modelClass}>;)V", null);
    // 生成方法时要注意方法参数的书写形式是 Ljava/lang/String;    V表示Void
    // 这是Java的参数类型，规则为前加"L",后加";"，涉及到泛型时要放在signature中
    // 如果在声明过程中有些规则不会写的，可以使用ClassVisitor访问你使用Java源码生成的class类，然后在visitMethod中把参数打印出来抄一下
    
    // 开始写方法代码
    methodVisitor.visitCode()
    // 要先访问传入的参数
    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
    // 将上一个参数传入到你要调用的方法里面
    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, clazzName, "init", "($Constants.TYPE_CLASS_MAP)V", false);
    // 这个写法也不用强行记忆，仍然是使用Java编译后代码，利用Java自带的javap 工具，执行命令
    // javap -c xxxx.class
    // 这里面有生成后的字节码表现形式，照着抄一遍就可以了
    
    // 由于前面用了COMPUTE_FRAMES，这里随便传
    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitInsn(Opcodes.RETURN);
    methodVisitor.visitEnd();
    classVisitor.visitEnd();
    // 最后写入到文件中就可以了
}
```
package router.air

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor


class OriginClassVisitor extends ClassVisitor{

    OriginClassVisitor(int api) {
        super(api)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        println("descriptor: " + descriptor + "     signature: " + signature)
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}
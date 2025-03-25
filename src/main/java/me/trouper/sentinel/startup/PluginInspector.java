package me.trouper.sentinel.startup;

import org.objectweb.asm.*;

public class PluginInspector extends ClassVisitor {
    private boolean found = false;

    public PluginInspector() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ("java/net/URLClassLoader".equals(superName)) {
            found = true;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (value instanceof String && ((String) value).contains("java.net.URLClassLoader")) {
            found = true;
        }
        super.visitLdcInsn(value);
    }

    public boolean isFound() {
        return found;
    }
}

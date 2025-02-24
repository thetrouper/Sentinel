package me.trouper.sentinel.startup;

import org.objectweb.asm.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Injection {

    public static boolean modifyJar(File inputJar, Class<?> runnableClass, File outputJar) {
        // Read the JAR file's manifest
        try {
            JarFile jarFile = new JarFile(inputJar);
            Manifest manifest = jarFile.getManifest();

            // Get the Main-Class from the manifest
            String mainClassName = manifest.getMainAttributes().getValue("Main-Class");

            if (mainClassName == null) {
                throw new IllegalStateException("Main-Class attribute not found in the manifest.");
            }

            // Prepare the output JAR and manifest
            JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(outputJar), manifest);

            // Add the Runnable class to the JAR (if it doesn't exist already)
            addRunnableClassToJar(jarFile, jarOut, runnableClass);

            // Copy over the existing JAR entries (excluding the original Main-Class and duplicate classes)
            copyJarEntries(jarFile, jarOut, mainClassName);

            // Modify the Main-Class's main method to call the Runnable
            modifyMainMethod(jarFile, jarOut, mainClassName, runnableClass.getName());

            // Close the output stream
            jarOut.close();
            jarFile.close();
        } catch (Exception e) {
            System.out.println("Could not patch your server jar! " + e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void addRunnableClassToJar(JarFile jarFile, JarOutputStream jarOut, Class<?> runnableClass) throws Exception {
        // Check if the Runnable class is already present in the JAR
        String runnableClassPath = runnableClass.getName().replace('.', '/') + ".class";
        if (isClassInJar(jarFile, runnableClassPath)) {
            System.out.println("Runnable class already exists in the JAR.");
            return; // Skip adding the class if it's already in the JAR
        }

        // Convert the class to a byte array
        byte[] classBytes = getClassBytes(runnableClass);

        // Create an entry for the Runnable class in the JAR
        JarEntry classEntry = new JarEntry(runnableClassPath);
        jarOut.putNextEntry(classEntry);

        // Write the class byte array to the JAR
        jarOut.write(classBytes);
        jarOut.closeEntry();
    }

    private static byte[] getClassBytes(Class<?> clazz) throws IOException {
        // Load the class file using ClassLoader and convert it to byte array
        InputStream inputStream = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class");
        if (inputStream == null) {
            throw new IOException("Class not found: " + clazz.getName());
        }

        // Read the class file into byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private static boolean isClassInJar(JarFile jarFile, String classPath) {
        // Check if the class already exists in the JAR
        JarEntry entry = jarFile.getJarEntry(classPath);
        return entry != null;
    }

    private static void copyJarEntries(JarFile jarFile, JarOutputStream jarOut, String mainClassName) throws IOException {
        // Iterate over the entries in the JAR and copy them over to the output JAR
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().equals("META-INF/MANIFEST.MF") && !entry.getName().equals(mainClassName.replace('.', '/') + ".class")) {
                // Skip manifest and main class file (to avoid duplication)
                jarOut.putNextEntry(entry);
                InputStream inputStream = jarFile.getInputStream(entry);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    jarOut.write(buffer, 0, len);
                }
                inputStream.close();
                jarOut.closeEntry();
            }
        }
    }

    private static void modifyMainMethod(JarFile jarFile, JarOutputStream jarOut, String mainClassName, String runnableClassName) throws IOException {
        // Modify the main method of the main class
        JarEntry entry = new JarEntry(mainClassName.replace('.', '/') + ".class");
        InputStream inputStream = jarFile.getInputStream(entry);

        // Use ASM to read and modify the bytecode
        ClassReader classReader = new ClassReader(inputStream);
        ClassWriter classWriter = new ClassWriter(0);

        ClassVisitor classVisitor = new MainMethodModifier(classWriter, runnableClassName);
        classReader.accept(classVisitor, 0);

        // Write the modified class back to the JAR
        byte[] modifiedClass = classWriter.toByteArray();

        jarOut.putNextEntry(entry);
        jarOut.write(modifiedClass);
        jarOut.closeEntry();

        inputStream.close();
    }

    public static class MainMethodModifier extends ClassVisitor {
        private final String runnableClassName;

        public MainMethodModifier(ClassWriter classWriter, String runnableClassName) {
            super(Opcodes.ASM9, classWriter);
            this.runnableClassName = runnableClassName;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            // Check if the method is the main method (public static void main)
            if (name.equals("main") && descriptor.equals("([Ljava/lang/String;)V")) {
                // Modify the main method to add a call to the Runnable
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM9, mv) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        // Insert code to invoke the Runnable
                        mv.visitTypeInsn(Opcodes.NEW, runnableClassName.replace('.', '/'));
                        mv.visitInsn(Opcodes.DUP);
                        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, runnableClassName.replace('.', '/'), "<init>", "()V", false);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, runnableClassName.replace('.', '/'), "run", "()V", false);
                    }
                };
            }
            // Return the original MethodVisitor for other methods
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}

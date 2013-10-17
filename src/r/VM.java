package r;

import java.io.*;
import java.lang.reflect.*;

class VM {
    static final String LIBRARY_PATH = "java.library.path";

    public static void addToJavaLibraryPath(File dir) {
        if (!dir.isDirectory()) { throw new IllegalArgumentException(dir + " is not a directory."); }
        String javaLibraryPath = System.getProperty(LIBRARY_PATH);
        System.setProperty(LIBRARY_PATH, javaLibraryPath + File.pathSeparatorChar + dir.getAbsolutePath());

        resetJavaLibraryPath();
    }

    public static void resetJavaLibraryPath() {
        // This hack is definitely JVM dependent and only works for hotspot
        synchronized (Runtime.getRuntime()) {
            try {
                Field field = ClassLoader.class.getDeclaredField("usr_paths");
                field.setAccessible(true);
                field.set(null, null);

                field = ClassLoader.class.getDeclaredField("sys_paths");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {

            } catch (IllegalAccessException e) {

            }
        }
    }
}

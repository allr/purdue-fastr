package r.runtime;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

import org.junit.*;

import r.nodes.exec.*;

public class TestNodes {

    @Test public void testReplaceChild() {
        // make sure that all node class that have children also define a replaceChild method

        String prefix = System.getProperty("allr.prefix", "bin");
        Path classfilesDir = Paths.get(prefix, "r");

        final int prefixSize = prefix.length() + 1;
        final int extLength = ".class".length();

        FileVisitor<Path> pathVisitor = new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.endsWith(".class")) {
                    String cname = file.toString();
                    String className = cname.substring(prefixSize, cname.length() - extLength).replaceAll("/", ".");
                    try {
                        Class rClass = Class.forName(className);
                        if (RNode.class.isAssignableFrom(rClass)) {
                            RNode.checkReplaceChild(rClass);
                        }
                    } catch (ClassNotFoundException e) {
                        Assert.fail("Cannot find class " + className + ": " + e);
                    } catch (UnsatisfiedLinkError e) {
                        System.err.println("Cannot load class " + className + ", assuming it is a native interface and ignoring: " + e);
                    }
                }

                return FileVisitResult.CONTINUE;
            }

        };
        try {
            Files.walkFileTree(classfilesDir, pathVisitor);
        } catch (IOException e) {
            Assert.fail("Cannot walk the source tree: " + e);
        }

        //PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{java,class}");
    }
}

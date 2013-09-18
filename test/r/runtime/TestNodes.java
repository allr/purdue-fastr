package r.runtime;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

import org.junit.*;

import r.nodes.exec.*;

public class TestNodes {

    @Test
    public void testReplaceChild() {
        // make sure that all node class that have children also define a replaceChild method

        String prefix = "bin/";
        Path classfilesDir = Paths.get(prefix + "r");


        FileVisitor pathVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String basename = file.getFileName().toString();
                if (basename.endsWith(".class")) {
                    int nelems = file.getNameCount();
                    StringBuilder cname = new StringBuilder();
                    for(int i = 1; i < nelems - 1; i++) {
                        if (i > 1) {
                            cname.append(".");
                        }
                        cname.append(file.getName(i));
                    }
                    if (nelems > 1) {
                        cname.append(".");
                    }
                    cname.append(basename.substring(0, basename.length() - ".class".length()));
                    String className = cname.toString();
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

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{java,class}");
    }
}

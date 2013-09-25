package r.builtins;

import java.io.File;
import java.util.*;
import java.util.regex.*;

import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

final class ListFiles extends CallFactory {

    static final CallFactory _ = new ListFiles("list.files", new String[]{"path", "pattern", "all.files", "full.names", "recursive", "ignore.case", "include.dirs"}, new String[]{});

    private ListFiles(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int pathPos = ai.position("path");
        final int patternPos = ai.position("pattern");
        final int allFilesPos = ai.position("all.files");
        final int fullNamesPos = ai.position("full.names");
        final int recursivePos = ai.position("recursive");
        final int ignoreCasePos = ai.position("ignore.case");
        final int includeDirsPos = ai.position("include.dirs");

        return new Builtin(call, names, exprs) {
            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                RString path = pathPos == -1 ? RString.BOXED_DOT : parsePath(args[pathPos], ast);
                String pattern = patternPos == -1 ? null : parsePattern(args[patternPos], ast);
                boolean allFiles = allFilesPos == -1 ? false : parseLogical(args[allFilesPos], ast, "all.files");
                boolean fullNames = fullNamesPos == -1 ? false : parseLogical(args[fullNamesPos], ast, "full.names");
                boolean recursive = recursivePos == -1 ? false : parseLogical(args[recursivePos], ast, "recursive");
                boolean ignoreCase = ignoreCasePos == -1 ? false : parseLogical(args[ignoreCasePos], ast, "ignore.case");
                boolean includeDirs = includeDirsPos == -1 ? false : parseLogical(args[includeDirsPos], ast, "include.dirs");
                RString res = perform(path, pattern, allFiles, fullNames, recursive, ignoreCase, includeDirs, ast);
                return res;
            }
        };
    }

    static String convertPattern(String patternString) { // HACK

        // TODO: find a better solution, this is a hack that can only work for simple cases
        // GNU-R uses the PCRE and the TRE libraries for regular expressions
        if (patternString == null) {
            return null;
        }
        if (patternString.contains(".*") || patternString.contains("[") || patternString.contains("(")) {
            return patternString; // probably a full regular expression
        }

        return ".*" + patternString .replaceAll("\\*", ".*").replaceAll("\\?", ".?") + ".*";
    }

    static RString perform(RString path, String patternString, boolean allFiles, boolean fullNames, boolean recursive, boolean ignoreCase, boolean includeDirs, ASTNode ast) {
        Pattern pattern = null;
        if (patternString != null) {
            try {
                String convertedPattern = convertPattern(patternString);
                pattern  = Pattern.compile(convertedPattern, ignoreCase ? Pattern.CASE_INSENSITIVE : 0); // FIXME: can add UNICODE_CASE
            } catch (PatternSyntaxException e) {
                throw RError.getInvalidRegexp(ast, "pattern");
            }
        }
        int pathSize = path.size();
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < pathSize; i++) {
            String dir = path.getString(i);
            list(dir, res, pattern, 0, allFiles, fullNames ? dir + File.separatorChar : "", recursive, ignoreCase, includeDirs);
        }
        Collections.sort(res);
        return RString.RStringFactory.getFor(res.toArray(new String[res.size()])); // FIXME: unnecessary copying here
    }

    private static int maxDepth = 100; // FIXME: GNU-R instead has a limit on the path name length

    // FIXME: could use Java NIO's Files.walkFileTree instead
    static void list(String path, ArrayList<String> res, Pattern pattern, int depth, boolean allFiles, String prefix, boolean recursive, boolean ignoreCase, boolean includeDirs) {

        java.io.File dir = new java.io.File(path);
        java.io.File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            String name = f.getName();
            // In accordance to GNU-R, files starting with a dot (linux hidden) are treated as hidden files in Windows too
            if ((f.isHidden() || name.startsWith(".")) && !allFiles) {
                continue;
            }
            if (recursive && f.isDirectory()) {
                if (depth < maxDepth) {
                    // instead of separator char, R always uses / as the file separator
                    list(path + File.separatorChar + f.getName(), res, pattern, depth + 1, allFiles, prefix + name + '/', recursive, ignoreCase, includeDirs);
                }
            }
            if (pattern != null) {
                Matcher m = pattern.matcher(name);
                if (!m.matches()) {
                    continue;
                }
            }
            if (recursive && !includeDirs && f.isDirectory()) {
                continue;
            }
            res.add(prefix + name);
        }
    }

    static RString parsePath(RAny arg, ASTNode ast) {
        if (!(arg instanceof RString)) {
            throw RError.getInvalidArgument(ast, "path");
        }
        return(RString) arg;
    }

    static String parsePattern(RAny arg, ASTNode ast) {
        if (arg instanceof RString) {
            RString rs = (RString) arg;
            if (rs.size() == 0) {
                return null;
            }
            String s = rs.getString(0);
            if (s != RString.NA) {
                if (s.length() > 0) {
                    return s;
                } else {
                    return null; // any file name matches an empty string
                }
            }
        }
        throw RError.getInvalidArgument(ast, "pattern");
    }

    static boolean parseLogical(RAny arg, ASTNode ast, String argName) {
        RLogical l = arg.asLogical();
        if (l.size() == 0) {
            throw RError.getInvalidArgument(ast, argName);
        }
        int value = l.getLogical(0);
        if (value == RLogical.NA) {
            throw RError.getInvalidArgument(ast, argName);
        }
        return value == RLogical.TRUE;
    }
}

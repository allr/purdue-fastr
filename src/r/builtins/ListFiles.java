package r.builtins;

import java.util.*;
import java.util.regex.*;

import r.Truffle.Frame;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * Argument ignoreCase is currently ignored. TODO fix
 */
final class ListFiles extends CallFactory {

    static final CallFactory _ = new ListFiles("list.files", new String[]{"path", "pattern", "all.files", "full.names", "recursive", "ignore.case", "include.dirs"}, new String[]{});

    private ListFiles(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @SuppressWarnings("unused") @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int pathPos = ai.position("path");
        final int patternPos = ai.position("pattern");
        final int allFilesPos = ai.position("all.files");
        final int fullNamesPos = ai.position("full.names");
        final int recursivePos = ai.position("recursive");
        final int ignoreCasePos = ai.position("ignore.case");
        final int includeDirsPos = ai.position("include.dirs");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                String path = get(params, pathPos, ".");
                String pattern = get(params, patternPos, null);
                boolean allFiles = get(params, allFilesPos, false);
                boolean fullNames = get(params, fullNamesPos, false);
                boolean recursive = get(params, recursivePos, false);
                boolean ingoreCase = get(params, ignoreCasePos, false);
                boolean includeDirs = get(params, includeDirsPos, false);
                String[] res = perform(path, pattern, allFiles, fullNames, recursive, ingoreCase, includeDirs);
                return new StringImpl(res);
            }
        };
    }

    static String get(RAny[] args, int position, String defaultValue) {
        if (position == -1) return defaultValue;
        RAny val = args[position];
        if (val instanceof ScalarStringImpl) return ((ScalarStringImpl) val).getString();
        if (val instanceof StringImpl) return ((StringImpl) val).getString(0);
        return defaultValue; //TODO Should convert stuff to strings?
    }

    static boolean get(RAny[] args, int position, boolean defaultValue) {
        if (position == -1) return defaultValue;
        RAny val = args[position];
        if (val instanceof ScalarLogicalImpl) return ((ScalarLogicalImpl) val).getBoolean();
        if (val instanceof LogicalImpl) return ((LogicalImpl) val).getBoolean(0);
        return defaultValue; //TODO Should convert stuff to strings?
    }

    static String[] perform(String path, String pattern, boolean allFiles, boolean fullNames, boolean recursive, boolean ignoreCase, boolean includeDirs) {
        java.io.File p = new java.io.File(path);
        java.io.File[] fs = p.listFiles();
        Vector<String> res = new Vector<String>();
        for (java.io.File f : fs) {
            String name = f.getName();
            if (f.isHidden() && !allFiles) continue;
            if (pattern != null && !pattern.equals("") && !Pattern.matches(pattern, name)) continue;
            if (f.isDirectory() && recursive) {
                String[] rec = perform(path + "/" + f.getName(), pattern, allFiles, fullNames, recursive, ignoreCase, includeDirs);
                for (String s : rec)
                    res.add(s);
            }
            if (f.isDirectory() && !includeDirs) continue;
            res.add(name);
        }
        return res.toArray(EMPTY);
    }

    static final String[] EMPTY = new String[]{};
}

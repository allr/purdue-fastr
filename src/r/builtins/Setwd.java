package r.builtins;

import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

import java.io.File;

// FIXME: is it safe to change user.dir ?
// FIXME: cannot make all the checks as on e.g. Linux (Java does not really allow to change the working directory)
public class Setwd extends CallFactory {

    static final CallFactory _ = new Setwd("setwd", new String[]{"dir"}, null);

    private Setwd(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int dirPos = ai.position("dir");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                String dir = parseDir(args[dirPos], ast);
                String res = perform(dir, ast);
                return RString.RStringFactory.getScalar(res);
            }
        };
    }

    static String perform(String dir, ASTNode ast) {
        String old = System.getProperty("user.dir");
        File f = new File(dir);
        if (!f.isDirectory()) { // FIXME: should check permissions, but probably not possible from Java
            throw RError.getCannotChangeDirectory(ast);
        }
        System.setProperty("user.dir", dir);
        return old;
    }

    static String parseDir(RAny arg, ASTNode ast) {
        if (arg instanceof RString) {
            RString rs = (RString) arg;
            if (rs.size() >= 1) {
                String s = rs.getString(0);
                if (s == RString.NA) {
                    throw RError.getMissingInvalid(ast);
                }
                return s;
            }
        }
        throw RError.getCharacterExpected(ast);
    }
}

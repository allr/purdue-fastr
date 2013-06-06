package r.builtins;

import java.io.*;

import org.antlr.runtime.*;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: only small part of the R semantics implemented, GNU-R has this implemented in R
public class Source extends CallFactory {


    static final CallFactory _ = new Source("source", new String[]{"file", "local", "echo", "print.eval", "verbose", "prompt.echo",
            "max.deparse.length", "chdir", "encoding", "continue.echo", "skip.echo", "keep.source"}, new String[] {"file"});

    private Source(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posFile = ia.position("file");
        final int posLocal = ia.position("local");
        if (ia.position("echo") != -1 || ia.position("print.eval") != -1 || ia.position("verbose") != -1 || ia.position("prompt.echo") != -1 ||
                ia.position("max.deparse.length") != -1 || ia.position("chdir") != -1 || ia.position("encoding") != -1 ||
                ia.position("continue.echo") != -1 || ia.position("skip.echo") != -1 || ia.position("keep.source") != -1) {
            Utils.nyi();
        }

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny fileArg = args[posFile];
                ANTLRStringStream input;
                if (fileArg instanceof RString) {
                    RString narg = (RString) fileArg;
                    if (narg.size() != 1) {
                        // TODO: this is not the same semantics as GNU-R, but there source is written in R
                        throw RError.getInvalidArgument(ast, "file");
                    }
                    String fileName = narg.getString(0);
                    try {
                        input = new ANTLRFileStream(fileName);
                    } catch (IOException e) {
                        throw RError.getCannotOpenFile(ast, fileName, e.toString());
                    }
                } else {
                    // TODO: add support for connection object and more
                    Utils.nyi("only file name supported");
                    return null;
                }

                ASTNode tree = RContext.parseFile(input);
                Frame targetFrame;
                if (posLocal == -1) {
                    targetFrame = null;
                } else {
                    RAny localArg = args[posLocal];
                    if (localArg instanceof REnvironment) {
                        targetFrame = ((REnvironment) localArg).frame();
                    } else if (localArg instanceof RLogical) {
                        boolean local = parseNonEnvironmentLocal(ast, localArg);
                        if (local) {
                            targetFrame = frame;
                        } else {
                            targetFrame = null;
                        }
                    } else {
                        throw RError.getMustBeTrueFalseEnvironment(ast, "local");
                    }
                }

                RFunction rootEnclosingFunction = targetFrame == null ? null : RFrameHeader.function(targetFrame);
                return (RAny) RContext.createRootNode(tree, rootEnclosingFunction).execute(targetFrame);
            }

        };
    }

    public static boolean parseNonEnvironmentLocal(ASTNode ast, RAny arg) {

        if (arg instanceof RLogical) {
            RLogical l = (RLogical) arg;
            if (l.size() == 1) {
                int v = l.getLogical(0);
                if (v == RLogical.TRUE) {
                    return true;
                }
                if (v == RLogical.FALSE) {
                    return false;
                }
            }
        }
        throw RError.getMustBeTrueFalseEnvironment(ast, "local");
    }

}

package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

public class Eval extends CallFactory {

    static final CallFactory _ = new Eval("eval", new String[]{"expr", "envir", "enclos"}, new String[] {"expr"});

    private Eval(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posExpr = ia.position("expr");
        final int posEnvir = ia.position("envir");
        final int posEnclos = ia.position("enclos");

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                Frame targetFrame = frame; // parent environment by default
                if (posEnvir != -1) {
                    RAny envirArg = args[posEnvir];
                    if (envirArg instanceof RList) {
                        Frame parentFrame;
                        if (posEnclos == -1) {
                            parentFrame = frame;
                        } else {
                            RAny enclosArg = args[posEnclos];
                            if (enclosArg instanceof RNull) {
                                parentFrame = null; // TODO: should be baseenv, not the global environment
                            } else if (enclosArg instanceof REnvironment) {
                                parentFrame = ((REnvironment) enclosArg).frame();
                            } else {
                                throw RError.getInvalidArgument(ast, "enclos");
                            }
                        }
                        targetFrame = EnvironmentImpl.Custom.createForList(parentFrame, (RList) envirArg);
                    } else if (envirArg instanceof REnvironment) {
                        targetFrame = ((REnvironment) envirArg).frame();
                    } else if (envirArg instanceof RNull) {
                        if (posEnclos == -1) {
                            targetFrame = frame;
                        } else {
                            RAny enclosArg = args[posEnclos];
                            if (enclosArg instanceof RNull) {
                                targetFrame = null; // TODO: should be baseenv, not the global environment
                            } else if (enclosArg instanceof REnvironment) {
                                targetFrame = ((REnvironment) enclosArg).frame();
                            } else {
                                throw RError.getInvalidArgument(ast, "enclos");
                            }
                        }
                    } else if (envirArg instanceof RInt || envirArg instanceof RDouble) {
                        RInt ienv = envirArg.asInt();
                        if (ienv.size() != 1) {
                            throw RError.getEnvirNotLengthOne(ast);
                        }
                        int i = ienv.getInt(0);
                        if (i == 0) {
                            // NOTE: the argument of 0 does not make sense, anyway
                            targetFrame = EnvironmentImpl.Custom.create(null, new EnvironmentImpl.Empty(), false, 0).frame();
                        } else if (i == -1) {
                            targetFrame = frame;
                        } else {
                            Utils.nyi(); // TODO: support arbitrary frame (or perhaps not - note the index is to call stack, not lexical scope)
                        }
                    } else {
                        throw RError.getInvalidArgument(ast, "envir");
                    }
                }
                RAny exprArg = args[posExpr];
                if (exprArg instanceof RLanguage) {
                    ASTNode exprAST = ((RLanguage) exprArg).get();
                    RFunction rootEnclosingFunction = targetFrame == null ? null : targetFrame.function();
                    return (RAny) RContext.createRootNode(exprAST, rootEnclosingFunction).execute(targetFrame);
                } else if (exprArg instanceof RSymbol) {
                    ASTNode exprAST = new r.nodes.ast.SimpleAccessVariable((RSymbol) exprArg);
                    RFunction rootEnclosingFunction = targetFrame == null ? null : targetFrame.function();
                    return (RAny) RContext.createRootNode(exprAST, rootEnclosingFunction).execute(targetFrame);
                } else {
                    return exprArg;
                }
            }
        };
    }

}

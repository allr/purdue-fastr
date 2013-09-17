package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "commandArgs"
 * 
 * <pre>
 * trailingOnly -- logical. Should only arguments after --args be returned?
 * </pre>
 */
final class CommandArgs extends CallFactory {

    static final CallFactory _ = new CommandArgs("commandArgs", new String[]{"trailingOnly"}, new String[]{});

    private CommandArgs(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        if (names.length == 0) { return new Builtin.Builtin0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return RString.RStringFactory.getFor(Console.trailingArgs);
            }
        }; }
        ensureArgName(call, "trailingOnly", names[0]);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny x) {
                RLogical l = x instanceof RLogical ? (RLogical) x : x.asLogical();
                int size = l.size();
                if (size == 0) { throw RError.getLengthZero(ast); }
                if (size > 1) {
                    RContext.warning(ast, RError.LENGTH_GT_1);
                }
                int trailingOnly = l.getLogical(0);
                if (trailingOnly == RLogical.TRUE) {
                    return RString.RStringFactory.getArray(Console.trailingArgs);
                } else if (trailingOnly == RLogical.FALSE) {
                    return RString.RStringFactory.getArray(Console.commandArgs);
                } else {
                    throw RError.getUnexpectedNA(ast); // not always the same error message as with GNU-R
                }
            }
        };
    }
}

package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: this would have been easier to write in R
//        GNU R has this written in R, but the code depends on too many things we don't support yet
final class Seq extends CallFactory {

    static final CallFactory _ = new Seq("seq", new String[]{"from", "to", "by", "length.out", "along.with", "..."}, new String[]{});

    Seq(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

        throw Utils.nyi("General case for seq to be implemented (in R?)");
    }
}

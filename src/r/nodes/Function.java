package r.nodes;

import java.util.*;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.data.RFunction.*;
import r.data.internal.*;
import r.nodes.tools.*;
import r.nodes.truffle.RNode;

public class Function extends ASTNode {

    final ArgumentList signature;
    final ASTNode body;

    private static final EnclosingSlot[] emptyReadSet = new EnclosingSlot[0];

    RFunction rfunction; // FIXME: is it ok this is not final?

    private static final boolean DEBUG_FUNCTIONS = false;

    Function(ArgumentList alist, ASTNode body) {
        this.signature = alist;
        this.body = updateParent(body);
    }

    public RFunction getRFunction() {
        return rfunction;
    }

    private void setRFunction(RFunction rfunction) {
        this.rfunction = rfunction;
    }

    public ArgumentList getSignature() {
        return signature;
    }

    public ASTNode getBody() {
        return body;
    }

    @Override public void accept(Visitor v) {
        v.visit(this);
    }

    @Override public void visit_all(Visitor v) {
        body.accept(v);
    }

    public static ASTNode create(ArgumentList alist, ASTNode body) {
        return new Function(alist, body);
    }

    @Override public String toString() {
        // FIXME: real R remembers the expression string for this
        StringBuilder str = new StringBuilder();
        str.append("function (");
        boolean first = true;
        for (ArgumentList.Entry a : signature) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            str.append(a.getName().pretty());
            ASTNode exp = a.getValue();
            if (exp != null) {
                str.append("=");
                str.append(exp.toString());
            }
        }
        str.append(") ");
        str.append(PrettyPrinter.prettyPrint(body));
        return str.toString();
    }

    private static void printAccesses(Set<RSymbol> read, Set<RSymbol> written) {
        StringBuilder str = new StringBuilder();
        str.append("Reads found in a function:");
        for (RSymbol s : read) {
            str.append(" ");
            str.append(s.pretty());
        }
        str.append("\nWrites found in a function:");
        for (RSymbol s : written) {
            str.append(" ");
            str.append(s.pretty());
        }
        Utils.debug(str.toString());
    }

    // note: the locallyWritten and locallyRead hash sets are modified input parameters
    public RFunction createImpl(RSymbol[] paramNames, RNode[] paramValues, RNode runnableBody, RFunction enclosing) {
        // note: we cannot read fnode.argNames() here as it is not yet initialized

        // find variables accessed
        Set<RSymbol> read = new HashSet<>();
        Set<RSymbol> written = new HashSet<>();
        findAccesses(read, written);
        if (DEBUG_FUNCTIONS) {
            printAccesses(read, written);
        }

        RSymbol[] writeSet = buildWriteSet(paramNames, written);
        EnclosingSlot[] readSet = buildReadSet(enclosing, read);

        FunctionImpl impl = new FunctionImpl(this, paramNames, paramValues, runnableBody, enclosing, writeSet, readSet);
        setRFunction(impl);
        return impl;
    }

    void findAccesses(Set<RSymbol> rs, Set<RSymbol> ws) {
        new FindAccesses().find(rs, ws);
    }

    private static RSymbol[] buildWriteSet(RSymbol[] argNames, Set<RSymbol> origWSet) {
        int maxSize = origWSet.size() + argNames.length;
        RSymbol[] writeSet = new RSymbol[maxSize];
        HashSet<RSymbol> args = new HashSet<RSymbol>(argNames.length);
        int i = 0;
        for (; i < argNames.length; i++) {
            RSymbol s = argNames[i];
            writeSet[i] = s;
            args.add(s);
        }
        for (RSymbol s : origWSet) {
            if (!args.contains(s)) {
                writeSet[i++] = s;
            }
        }
        if (i < maxSize) {
            RSymbol[] bigSet = writeSet;
            writeSet = new RSymbol[i];
            System.arraycopy(bigSet, 0, writeSet, 0, i);
        }
        // NOTE: write set does not have duplicates
        return writeSet;
    }

    private static EnclosingSlot[] buildReadSet(RFunction parent, Set<RSymbol> origRSet) {
        // build read set
        if (parent == null || origRSet.isEmpty()) {
            if (DEBUG_FUNCTIONS) {
                if (parent == null) {
                    Utils.debug("Read-set is empty because there is no lexically enclosing (parent) function");
                }
            }
            return emptyReadSet;
        }
        ArrayList<EnclosingSlot> rsl = new ArrayList<>();
        for (RSymbol s : origRSet) {
            RFunction p = parent;
            int hops = 1;
            while (p != null) {
                FrameSlot slot = p.localSlot(s);
                if (slot != null) {
                    rsl.add(new EnclosingSlot(s, hops, slot));
                    break;
                }
                p = p.enclosingFunction();
                hops++;
            }
        }
        return rsl.toArray(new EnclosingSlot[0]); // FIXME: rewrite this to get rid of allocation/copying
    }

    class FindAccesses extends BasicVisitor implements Visitor {

        Set<RSymbol> read;
        Set<RSymbol> written;

        public void find(Set<RSymbol> rs, Set<RSymbol> ws) {
            this.read = rs;
            this.written = ws;

            visit_all(this); // does a function body
            // FIXME: should visit_all visit the default expressions on its own?
            ArgumentList al = getSignature();
            for (ArgumentList.Entry e : al) {
                ASTNode val = e.getValue();
                if (val != null) {
                    val.visit_all(this);
                }
                // note: formal arguments are added to write set elsewhere
            }
        }

        @Override public void visit(SimpleAccessVariable readVariable) {
            read.add(readVariable.getSymbol());
        }

        @Override public void visit(SimpleAssignVariable assign) {
            written.add(assign.getSymbol());
            assign.visit_all(this); // visit the rhs expression
        }

        @Override public void visit(Function function) {}

        @Override public void visit(FunctionCall functionCall) {
            read.add(functionCall.getName());
            functionCall.visit_all(this); // visit the arguments passed (simple access variable)
            if (functionCall.isAssignment()) {
                SimpleAccessVariable varAccess = (SimpleAccessVariable) functionCall.getArgs().first().getValue();
                written.add(varAccess.getSymbol());
            }
        }

        @Override public void visit(For n) {
            written.add(n.getCVar());
            n.visit_all(this);
        }

        @Override public void visit(UpdateVector n) {
            AccessVector a = n.getVector();
            ASTNode v = a.getVector();
            if (!(v instanceof SimpleAccessVariable)) {
                Utils.nyi("unsupported");
            }
            written.add(((SimpleAccessVariable) v).getSymbol());
            n.visit_all(this);
        }
    }
}

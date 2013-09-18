package r.nodes.ast;

import java.util.*;

import r.data.*;

public interface ArgumentList extends Collection<ArgumentList.Entry> {

    Entry first();

    void add(ASTNode value);

    void add(String name, ASTNode value);

    void add(RSymbol name, ASTNode value);

    interface Entry {

        RSymbol getName();

        ASTNode getValue();
    }

    ASTNode getNode(int i);

    class Default extends ArrayList<Entry> implements ArgumentList {

        private static final long serialVersionUID = 1L;

        @Override
        public void add(ASTNode e) {
            super.add(new DefaultEntry(null, e));
        }

        @Override
        public void add(RSymbol name, ASTNode value) {
            super.add(new DefaultEntry(name, value));
        }

        @Override
        public void add(String name, ASTNode value) {
            add(RSymbol.getSymbol(name), value);
        }

        @Override
        public Entry first() {
            return this.get(0);
        }

        @Override
        public ASTNode getNode(int i) {
            return this.get(i).getValue();
        }


        public static void updateParent(ASTNode parent, ArgumentList list) {
            for (Entry e : list) {
                parent.updateParent((ASTNode) e);
            }
        }

        public static final class DefaultEntry extends ASTNode implements Entry {

            RSymbol name;
            ASTNode value;

            private DefaultEntry(RSymbol n, ASTNode v) {
                name = n;
                value = updateParent(v);
            }

            @Override
            public RSymbol getName() {
                return name;
            }

            @Override
            public ASTNode getValue() {
                return value;
            }

            @Override
            public void visit_all(Visitor v) {
                getValue().accept(v);
            }

            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }
        }
    }
}

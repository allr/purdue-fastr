package r.nodes;

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

    class Default extends ArrayList<Entry> implements ArgumentList {

        private static final long serialVersionUID = 1L;

        @Override
        public void add(ASTNode e) {
            add(new DefaultEntry(null, e));
        }

        @Override
        public void add(RSymbol name, ASTNode value) {
            add(new DefaultEntry(name, value));
        }

        @Override
        public void add(String name, ASTNode value) {
            add(RSymbol.getSymbol(name), value);
        }

        @Override
        public Entry first() {
            return this.get(0);
        }

        private static final class DefaultEntry implements Entry {

            RSymbol name;
            ASTNode value;

            private DefaultEntry(RSymbol n, ASTNode v) {
                name = n;
                value = v;
            }

            @Override
            public RSymbol getName() {
                return name;
            }

            @Override
            public ASTNode getValue() {
                return value;
            }

        }
    }
}

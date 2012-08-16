package r.nodes;

import java.util.*;

import r.data.*;

public interface ArgumentList extends Collection<ArgumentList.Entry> {

    Entry first();

    void add(Node value);

    void add(String name, Node value);

    void add(RSymbol name, Node value);

    interface Entry {

        RSymbol getName();

        Node getValue();
    }

    class Default extends ArrayList<Entry> implements ArgumentList {

        private static final long serialVersionUID = 1L;

        @Override
        public void add(Node e) {
            add(new DefaultEntry(null, e));
        }

        @Override
        public void add(RSymbol name, Node value) {
            add(new DefaultEntry(name, value));
        }

        @Override
        public void add(String name, Node value) {
            add(RSymbol.getSymbol(name), value);
        }

        @Override
        public Entry first() {
            return this.get(0);
        }

        private static final class DefaultEntry implements Entry {

            RSymbol name;
            Node value;

            private DefaultEntry(RSymbol n, Node v) {
                name = n;
                value = v;
            }

            @Override
            public RSymbol getName() {
                return name;
            }

            @Override
            public Node getValue() {
                return value;
            }

        }
    }
}

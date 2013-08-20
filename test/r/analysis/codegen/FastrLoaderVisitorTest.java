package r.analysis.codegen;

import com.oracle.truffle.api.frame.Frame;
import org.junit.Test;
import r.analysis.visitor.*;
import r.nodes.truffle.RNode;

import static junit.framework.Assert.*;




class Node extends RNode {

    @Override
    public Object execute(Frame frame) {
        return null;
    }
}

class SingleNestedNode extends Node {
    Node child;
    public SingleNestedNode(Node child) {
        this.child = adoptChild(child);
    }
}

class UnvisitedNestedNode extends Node {
    @DoNotVisit
    Node child;
    public UnvisitedNestedNode(Node child) {
        this.child = adoptChild(child);
    }
}

class DoubleNestedNode extends Node {
    Node child1;
    Node child2;
    public DoubleNestedNode(Node child1, Node child2) {
        this.child1 = adoptChild(child1);
        this.child2 = adoptChild(child2);
    }
}

class ArrayNestedNode extends Node {
    Node[] children;
    public ArrayNestedNode(int size) {
        children = new Node[size];
        for (int i = 0; i < size; ++i)
            children[i] = adoptChild(new Node());
    }
}




// ---------------------------------------------------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------------------------------------------------

public class FastrLoaderVisitorTest {

    @Test
    public void visitorWorksForSingleNode() {
        final Node n = new Node();
        n.accept(new NodeVisitor() {
            @Override
            public boolean visit(RNode node) {
                assertTrue(node == n);
                return true;
            }
        });
    }

    @Test
    public void visitorWorksForNestedNodes() {
        final Node n = new Node();
        final SingleNestedNode n2 = new SingleNestedNode(n);
        n2.accept(new NodeVisitor() {
            int i = 0;
            @Override
            public boolean visit(RNode node) {
                switch (i) {
                    case 0:
                        assertTrue(node == n2);
                        break;
                    case 1:
                        assertTrue(node == n);
                        break;
                }
                ++i;
                return true;
            }
        });
    }

    @Test
    public void visitorSkipsChildrenIfFalseReturned() {
        final Node n = new Node();
        final SingleNestedNode n2 = new SingleNestedNode(n);
        n2.accept(new NodeVisitor() {
            @Override
            public boolean visit(RNode node) {
                assertTrue(node != n);
                return false;
            }
        });
    }
    @Test
    public void visitorSkipsNullFields() {
        final SingleNestedNode n2 = new SingleNestedNode(null);
        n2.accept(new NodeVisitor() {
            @Override
            public boolean visit(RNode node) {
                assertNotNull(node);
                return true;
            }
        });
    }

    @Test
    public void visitorOnUnvisitedNodes() {
        final Node n = new Node();
        final UnvisitedNestedNode n2 = new UnvisitedNestedNode(n);
        n2.accept(new NodeVisitor() {
            @Override
            public boolean visit(RNode node) {
                assertTrue(node != n);
                return true;
            }
        });

    }

    abstract static class CountingNodeVisitor implements NodeVisitor {
        int[] count;
    }

    @Test
    public void visitorWorksOnTrees() {
        final Node n = new Node();
        final DoubleNestedNode n2 = new DoubleNestedNode(n, n);
        final DoubleNestedNode n3 = new DoubleNestedNode(n2, n2);
        final DoubleNestedNode n4 = new DoubleNestedNode(n3, n);
        CountingNodeVisitor v = new CountingNodeVisitor() {
            @Override
            public boolean visit(RNode node) {
                if (node == n)
                    count[0] += 1;
                else if (node == n2)
                    count[1] += 1;
                else if (node == n3)
                    count[2] += 1;
                else
                    count[3] += 1;
                return true;
            }

            {
                count = new int[4];
            }
        };
        n4.accept(v);
        assertTrue(v.count[3] == 1);
        assertTrue(v.count[2] == 1);
        assertTrue(v.count[1] == 2);
        assertTrue(v.count[0] == 5);
    }

    @Test
    public void visitorWorksOnArrays() {
        final ArrayNestedNode n = new ArrayNestedNode(10);
        n.accept(new NodeVisitor() {
            int i = -1;
            @Override
            public boolean visit(RNode node) {
                if (i == -1)
                    assertTrue(node == n);
                else
                    assertTrue(node == n.children[i]);
                ++i;
                return true;
            }
        });
    }

    @Test
    public void visitorWorksOnNullArrays() {
        final ArrayNestedNode n = new ArrayNestedNode(10);
        n.children = null;
        n.accept(new NodeVisitor() {
            int i = -1;
            @Override
            public boolean visit(RNode node) {
                assertTrue(node == n);
                return true;
            }
        });
    }

    @Test
    public void visitorWorksOnArraysWithNulls() {
        final ArrayNestedNode n = new ArrayNestedNode(10);
        n.children[5] = null;
        n.children[8] = null;
        n.accept(new NodeVisitor() {
            int i = -1;
            @Override
            public boolean visit(RNode node) {
                if (i == -1)
                    assertTrue(node == n);
                else
                    assertTrue(node == n.children[i]);
                ++i;
                if (i == 5)
                    ++i;
                else if (i == 8)
                    ++i;
                return true;
            }
        });
    }
}



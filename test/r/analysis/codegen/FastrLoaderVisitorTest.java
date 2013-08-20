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

    @Test
    public void visitorWorksOnTrees() {
        final Node n = new Node();
    }



}



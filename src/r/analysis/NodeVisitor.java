package r.analysis;

import r.nodes.truffle.RNode;

public abstract class NodeVisitor {

    /** Visitor callback method for the nodes. For each node the node itself is visited and then all its children are
     * too, each calling this method.
     *
     * By default, true should be returned for normal operation. However, if any child nodes of the current node (the
     * argument) should not be visited, returning false terminates the tree walk for the current branch.
     */
    public abstract boolean visit(RNode node);
}

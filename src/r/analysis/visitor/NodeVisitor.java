package r.analysis.visitor;

import r.nodes.truffle.RNode;

/** Base class for analysis node visitors.
 *
 * The analysis node visitor is used to perform various analyses. The actual traversal is implemented in the nodes
 * themselves by overriding the accept method. If such method is not defined, it is automatically created during the
 * class loading process - see FastrLoader for that.
 */
public interface NodeVisitor {

    /** Visitor method for the nodes.
     *
     * Takes the node to visit as an argument and returns True if the children of the node should also be visited, false
     * otherwise.
     */
    public boolean visit(RNode node);
}

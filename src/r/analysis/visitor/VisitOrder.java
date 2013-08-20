package r.analysis.visitor;

import java.lang.annotation.*;

/** Defines the order in which the fields of a node should be visited.
 *
 * The index can be any integer number, fields are visited in ascending order. Please note that the order of the visits
 * is only relevant for fields defined in a single class. The intra-class rule is simple. First the node itself is
 * visited (RNode), then for each level of inheritance the fields are visited in their own order and finally the fields
 * in the actual type are visited in their defined order.
 */
@Target(ElementType.FIELD)
public @interface VisitOrder {
    int index();
}

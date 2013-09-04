package r.analysis;

import r.analysis.nodes.CounterNode;
import r.data.*;
import r.nodes.ASTNode;
import r.nodes.tools.*;
import r.nodes.truffle.RNode;

/** Helper class for analysis tests.
 *
 * Contains execution methods focused on repeated executions and execution trees rather than results.
 */
public class AnalysisTestBase {

    static Truffleize truffleize = new Truffleize();

    /** Parses the given input and executes in N times. Returns the execution tree after the n-th execution.
     */
    static Truffleize.LazyRootTree execNTimes(String input, int n) {
        try {
            ASTNode ast = TestPP.parse(input);
            RNode root =  truffleize.createLazyRootTree(ast);
            while (n-- > 0)
                root.execute(null);
            return (Truffleize.LazyRootTree) root;
        } catch (Exception e) {
            return null;
        }
    }

    /** Parses and executes the given input.
     *
     * Only once. Returns the execution tree, not the result itself.
     */
    static Truffleize.LazyRootTree exec(String input) {
        return execNTimes(input, 1);
    }

    /** Parses and executes the given input enough times to make all counter nodes reach their threshold.
     *
     * In reality executes the tree CounterNode.THRESHOLD times plus one. Returns the execution tree after the last execution.
     */
    static Truffleize.LazyRootTree execEnoughTimes(String input) {
        return execNTimes(input, CounterNode.THRESHOLD + 1);
    }

    /** Executes the given input and returns its value rather than the execution tree.
     */
    static RAny execResult(String input) {
        try {
            ASTNode ast = TestPP.parse(input);
            RNode root =  truffleize.createLazyRootTree(ast);
            return execResult(root);
        } catch (Exception e) {
            return null;
        }
    }

    /** Executes the given tree and returns its result.
     */
    static RAny execResult(RNode tree) {
        try {
            return (RAny) tree.execute(null);
        } catch (Exception e) {
            return null;
        }

    }

    static boolean equalsDouble(RNode tree, double expected) {
        try {
            RDouble d = (RDouble) execResult(tree);
            return (d.size() == 1) && (d.getDouble(0) == expected);
        } catch (Exception e) {
            return false;
        }
    }

    static boolean equalsDouble(String input, double expected) {
        try {
            RDouble d = (RDouble) execResult(input);
            return (d.size() == 1) && (d.getDouble(0) == expected);
        } catch (Exception e) {
            return false;
        }
    }

    static boolean equalsInt(RNode tree, double expected) {
        try {
            RInt d = (RInt) execResult(tree);
            return (d.size() == 1) && (d.getInt(0) == expected);
        } catch (Exception e) {
            return false;
        }
    }

    static boolean equalsInt(String input, double expected) {
        try {
            RInt d = (RInt) execResult(input);
            return (d.size() == 1) && (d.getInt(0) == expected);
        } catch (Exception e) {
            return false;
        }
    }


}

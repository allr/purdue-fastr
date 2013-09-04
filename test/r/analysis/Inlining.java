package r.analysis;

import org.junit.Test;
import r.analysis.nodes.*;
import r.data.*;
import r.nodes.tools.*;
import r.nodes.truffle.FunctionCall;

import static org.junit.Assert.assertTrue;


public class Inlining extends AnalysisTestBase {

    @Test
    public void noArgsNoWritesHappens() {
        exec("f <- function() { 1 }; ");
        Truffleize.LazyRootTree n = execEnoughTimes("f()");
        assertTrue( n.getNode() instanceof GuardHolder);
        RSymbol.resetTable();
    }

    @Test
    public void noArgsNoWritesWorks() {
        exec("f <- function() { 1 }; ");
        Truffleize.LazyRootTree n = execEnoughTimes("f()");
        assertTrue( equalsDouble("f()", 1));
        RSymbol.resetTable();
    }

    @Test
    public void noArgsNoWritesWithReadHappens() {
        exec("f <- function() { a }; a = 1; ");
        Truffleize.LazyRootTree n = execEnoughTimes("f()");
        assertTrue( n.getNode() instanceof GuardHolder);
        RSymbol.resetTable();
    }

    @Test
    public void noArgsNoWritesWithReadWorks() {
        exec("f <- function() { a }; a = 42; ");
        Truffleize.LazyRootTree n = execEnoughTimes("f()");
        assertTrue( equalsDouble("f()", 42));
        RSymbol.resetTable();
    }

    @Test
    public void noArgsNoWritesChangingReadWorks() {
        exec("f <- function() { a }; a = 1; ");
        Truffleize.LazyRootTree n = execEnoughTimes("a = a + f()");
        exec("a = 1");
        n.execute(null);
        n.execute(null);
        n.execute(null);
        assertTrue(equalsDouble("f()", 8));
        RSymbol.resetTable();
    }

    @Test
    public void noArgsNoWritesRevertWhenFunctionUnstable() {
        exec("f <- function() { 1 }; ");
        Truffleize.LazyRootTree n = execEnoughTimes("f()");
        assertTrue(n.getNode() instanceof GuardHolder);
        exec("f <- function() { 2 }; ");
        assertTrue( equalsDouble(n, 2));
        assertTrue(n.getNode() instanceof FunctionCall);
    }

}
package r;

import org.antlr.runtime.*;
import r.builtins.internal.Random;
import r.data.*;
import r.data.internal.DoubleImpl;
import r.nodes.ASTNode;
import r.parser.*;

public class sandbox {

    static final int size = 100000000;
    static final int vsize = 10;




    public static Object f() {
        DoubleImpl aa = new DoubleImpl(vsize);
        DoubleImpl bb = new DoubleImpl(vsize);
        Object result = null;
        for (int i = 0; i < size; ++i) {
            final double[] l = aa.getContent();
            final double[] r = bb.getContent();
            final double[] res = new double[l.length];
            for (int j = 0; j < l.length; ++j) {
                double a = l[j];
                double b = r[j];
                double c = a + b;
                if (RDouble.RDoubleUtils.isNA(c)) {
                    if (RDouble.RDoubleUtils.isNA(a) || RDouble.RDoubleUtils.isNA(b)) {
                        res[j] = RDouble.NA;
                    }
                } else {
                    res[j] = c;
                }
            }
            result = RDouble.RDoubleFactory.getFor(res, null, null, null);
        }
        return result;
    }



    public static void main(String[] args) {
        try {
            RLexer lexer = new RLexer();
            RParser parser = new RParser(null);
            parser.reset();
            lexer.resetIncomplete();
            lexer.setCharStream(new ANTLRStringStream("f3 <- function() {\n" +
                    "    x = 3\n" +
                    "}\n" +
                    "\n" +
                    "f3()\n" +
                    "f3()\n" +
                    "f3()\n"));
            parser.setTokenStream(new CommonTokenStream(lexer));
            ASTNode astNode = parser.interactive();
            Random.resetSeed(); // RESETS RANDOM SEED
            System.out.println(RContext.eval(astNode, true));
        } catch (RecognitionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    /*    for (int i = 0; i < 10; ++i) {
            long t = System.currentTimeMillis();
            f();
            t = System.currentTimeMillis() - t;
            System.out.println("Iteration "+i+" took "+(t/1000.0)+" [s]");
        } */
    }

}

/*
s = as.vector(array(1,c(10)))
inc = as.vector(array(1,c(10)))

f2 = function(b) {
    b = b + s
    s <<- s + inc
    b
}

f = function(a) {
    x = _timerStart();
    for (i in 1:10) {
        a = a + f2(a)
    }
    s <<- a
    x
}

a = as.vector(array(0,c(10)))
f(a)
a = as.vector(array(0,c(10)))
s = as.vector(array(1,c(10)))
_timerEnd(f(a),"tmr")

*/
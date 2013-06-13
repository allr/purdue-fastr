package r;

import org.antlr.runtime.*;
import r.builtins.internal.Random;
import r.data.*;
import r.data.internal.DoubleImpl;
import r.nodes.ASTNode;
import r.parser.*;

public class sandbox {

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

    }

}


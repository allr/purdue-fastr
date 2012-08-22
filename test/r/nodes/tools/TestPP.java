package r.nodes.tools;

import junit.framework.*;

import org.antlr.runtime.*;
import org.junit.Test;

import r.nodes.*;
import r.parser.*;

public class TestPP {

    static RLexer lexer = new RLexer();
    static RParser parser = new RParser(null);
    static PrettyPrinter pp = PrettyPrinter.getStringPrettyPrinter();

    public static ASTNode parse(String input) throws RecognitionException {
        parser.reset();
        lexer.setCharStream(new ANTLRStringStream(input));
        parser.setTokenStream(new CommonTokenStream(lexer));
        return parser.interactive();
    }

    private static void assertPP(String input) throws RecognitionException {
        assertPP(input, input);
    }

    private static void assertPP(String input, String expected) throws RecognitionException {
        pp.print(parse(input));
        Assert.assertEquals(expected, pp.toString());
    }

    @Test(expected = RecognitionException.class)
    public void testParseError1() throws RecognitionException {
        parse("(");
    }

    @Test
    public void testBoolean() throws RecognitionException {
        assertPP("TRUE");
        assertPP("FALSE");
        assertPP("!TRUE");
        assertPP("!FALSE");
        assertPP("!!TRUE");
        assertPP("!!FALSE");
    }

    @Test
    public void testOperatorPrecedence1() throws RecognitionException {
        assertPP("1L + 2L * 3L + 4L");
    }

    @Test
    public void testOperatorPrecedence2() throws RecognitionException {
        assertPP("1L + 2L * 3L * 4L * 5L + 6L");
    }

    @Test
    public void testOperatorPrecedence3() throws RecognitionException {
        assertPP("(1+2)*(3+4)", "(1.0 + 2.0) * (3.0 + 4.0)");
    }

    @Test
    public void testOperatorAssociativity1() throws RecognitionException {
        assertPP("1.0 * 2.0 * 3.0 + 2.0 * 3.0 * 4.0 + 3.0 * 4.0 * 5.0");
        // assertPP("1.0 * (2.0 * 3.0) + 2.0 * 3.0 * 4.0 + 3.0 * 4.0 * 5.0\n");
    }
}

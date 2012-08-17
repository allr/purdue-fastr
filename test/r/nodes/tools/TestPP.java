package r.nodes.tools;

import java.io.*;

import junit.framework.*;

import org.antlr.runtime.*;
import org.junit.Test;

import r.nodes.*;
import r.parser.*;

public class TestPP {

    static RLexer lexer = new RLexer();
    static RParser parser = new RParser(null);

    private static Node parse(String input) throws RecognitionException {
        parser.reset();
        lexer.setCharStream(new ANTLRStringStream(input));
        parser.setTokenStream(new CommonTokenStream(lexer));
        return parser.interactive();
    }

    private static void assertPP(String input) throws RecognitionException {
        assertPP(input, input);
    }

    private static void assertPP(String input, String expected) throws RecognitionException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new PrettyPrinter(new PrintStream(os)).print(parse(input));
        Assert.assertEquals(expected, os.toString());
    }

    @Test(expected = RecognitionException.class)
    public void testParseError1() throws RecognitionException {
        parse("(");
    }

    @Test
    public void testBoolean() throws RecognitionException {
        assertPP("TRUE\n");
        assertPP("!TRUE\n");
        assertPP("FALSE\n");
        assertPP("!FALSE\n");
    }


    @Test
    public void testOperatorPrecedence1() throws RecognitionException {
        assertPP("1L + 2L * 3L + 4L\n");
    }

    @Test
    public void testOperatorPrecedence2() throws RecognitionException {
        assertPP("1L + 2L * 3L * 4L * 5L + 6L\n");
    }

    @Test
    public void testOperatorPrecedence3() throws RecognitionException {
        assertPP("(1+2)*(3+4)", "(1.0 + 2.0) * (3.0 + 4.0)\n");
    }
}

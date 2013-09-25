package r.builtins;

import java.io.*;
import java.util.*;

import r.*;
import r.Convert.ConversionStatus;
import r.Console;
import r.data.*;
import r.data.RComplex.Complex;
import r.data.internal.*;
import r.data.internal.Connection.FileConnection;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "scan"
 *
 * <pre>
 * file -- the name of a file to read data values from. If the specified file is "", then input
 *     is taken from the keyboard (or whatever stdin() reads if input is redirected or R is embedded).
 *     (In this case input can be terminated by a blank line or an EOF signal, Ctrl-D on Unix and Ctrl-Z
 *     on Windows.)Otherwise, the file name is interpreted relative to the current working directory
 *     (given by getwd()), unless it specifies an absolute path. Tilde-expansion is performed where
 *     supported. When running R from a script, file="stdin" can be used to refer to the process's stdin file stream.
 *     As from R 2.10.0 this can be a compressed file (see file). Alternatively, file can be a connection,
 *     which will be opened if necessary, and if so closed at the end of the function call. Whatever mode
 *     the connection is opened in, any of LF, CRLF or CR will be accepted as the EOL marker for a line and so will match sep = "\n".
 *     file can also be a complete URL. (For the supported URL schemes, see the URLs section of the help for url.)
 *     To read a data file not in the current encoding (for example a Latin-1 file in a UTF-8 locale or conversely) use a
 *     file connection setting its encoding argument (or scan's fileEncoding argument).
 * what -- the type of what gives the type of data to be read. The supported types are logical, integer, numeric, complex,
 *     character, raw and list. If what is a list, it is assumed that the lines of the data file are records each containing
 *     length(what) items (fields) and the list components should have elements which are one of the first six types
 *     listed or NUL
 * nmax -- integer: the maximum number of data values to be read, or if what is a list, the maximum number of records
 *     to be read. If omitted or not positive or an invalid value for an integer (and nlines is not set to
 *     a positive value), scan will read to the end of file.
 * n -- integer: the maximum number of data values to be read, defaulting to no limit. Invalid values will be ignored.
 * sep -- by default, scan expects to read white-space delimited input fields. Alternatively, sep can be used to specify
 *     a character which delimits fields. A field is always delimited by an end-of-line marker unless it is quoted.
 *     If specified this should be the empty character string (the default) or NULL or a character string containing
 *     just one single-byte character.
 * quote -- the set of quoting characters as a single character string or NULL. In a multibyte locale the quoting
 *     characters must be ASCII (single-byte).
 * dec -- decimal point character. This should be a character string containing just one single-byte character.
 *     (NULL and a zero-length character vector are also accepted, and taken as the default.)
 * skip -- the number of lines of the input file to skip before beginning to read data values.
 * nlines -- if positive, the maximum number of lines of data to be read.
 * na.strings -- character vector. Elements of this vector are to be interpreted as missing (NA) values. Blank
 *      fields are also considered to be missing values in logical, integer, numeric and complex fields.
 * flush -- logical: if TRUE, scan will flush to the end of the line after reading the last of the fields requested.
 *     This allows putting comments after the last field, but precludes putting more that one record on a line.
 * fill -- logical: if TRUE, scan will implicitly add empty fields to any lines with fewer fields than implied by what.
 * strip.white -- vector of logical value(s) corresponding to items in the what argument. It is used only when sep has been
 *      specified, and allows the stripping of leading and trailing 'white space' from character fields (numeric fields are always stripped).
 *      Note: white space inside quoted strings is not stripped. If strip.white is of length 1, it applies to all fields; otherwise, if
 *      strip.white[i] is TRUE and the i-th field is of mode character (because what[i] is) then the leading and trailing
 *      unquoted white space from field i is stripped.
 * quiet -- logical: if FALSE (default), scan() will print a line, saying how many items have been read.
 * blank.lines.skip -- logical: if TRUE blank lines in the input are ignored, except when counting skip and nlines.
 * multi.line -- logical. Only used if what is a list. If FALSE, all of a record must appear on one line
 *      (but more than one record can appear on a single line). Note that using fill = TRUE implies that a record
 *      will be terminated at the end of a line.
 * comment.char -- character: a character vector of length one containing a single character or an empty string.
 *      Use "" to turn off the interpretation of comments altogether (the default).
 * allowEscapes -- logical. Should C-style escapes such as \n be processed (the default) or read verbatim? Note that
 *      if not within quotes these could be interpreted as a delimiter (but not as a comment character).
 *      The escapes which are interpreted are the control characters \a, \b, \f, \n, \r, \t, \v and octal and hexadecimal
 *      representations like \ 040 and \ 0x2A. Any other escaped character is treated as itself, including backslash. Note that
 *      Unicode escapes (starting \ u or \ U: see Quotes) are never processed.
 * fileEncoding -- character string: if non-empty declares the encoding used on a file (not a connection nor the keyboard) so
 *      the character data can be re-encoded.
 * encoding -- encoding to be assumed for input strings. If the value is "latin1" or "UTF-8" it is used to mark character
 *      strings as known to be in Latin-1 or UTF-8: it is not used to re-encode the input
 * text -- character string: if file is not supplied and this is, then data are read from the value of text via a text connection.
 * </pre>
 */
// TODO: a very incomplete implementation, the full method has 21 parameters
// note also that some of the current implementation will likely have to be rewritten for the full set of features
final class Scan extends CallFactory {

    static final CallFactory _ = new Scan("scan", new String[]{"file", "what", "nmax", "quiet"}, new String[]{});

    private Scan(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final ConnectionMode defaultMode = ConnectionMode.get("r");
        final int posWhat = ia.position("what");
        final int posNmax = ia.position("nmax");
        final int posQuiet = ia.position("quiet");
        final int posFile = ia.position("file");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny what = posWhat != -1 ? args[posWhat] : RDouble.EMPTY;
                int nmax = posNmax != -1 ? parseNMax(args[posNmax], ast) : -1;
                boolean quiet = posQuiet != -1 ? parseQuiet(args[posQuiet]) : false;

                if (what instanceof RList) { throw Utils.nyi("list not yet implemented"); }
                if (what instanceof RNull) { throw RError.getInvalidArgument(ast, "what"); }
                Connection con = null;
                boolean wasOpen = false;

                if (posFile == -1) { throw Utils.nyi("stdin, text"); } // FIXME: similar code to that in readLines, extract?
                RAny conArg = args[posFile];
                if (conArg instanceof RString) {
                    String description = File.getScalarString(conArg, ast, "file");
                    con = FileConnection.createOpened(description, defaultMode, ast);
                } else if (conArg instanceof RInt) {
                    // FIXME: check if it is a connection once attributes are implemented
                    RInt iarg = (RInt) conArg;
                    if (iarg.size() != 1) { throw RError.getNotConnection(ast, "file"); }
                    int handle = iarg.getInt(0);
                    con = RContext.getConnection(handle);
                    Utils.check(con != null);
                    if (con.isOpen()) {
                        ConnectionMode mode = con.currentMode();
                        if (!mode.read()) { throw RError.getCannotReadConnection(ast); }
                        wasOpen = true;
                    } else {
                        con.open(defaultMode, ast);
                    }
                }
                try {
                    // TODO: replace this primitive scanning by something more general
                    // note that we cannot simply use Scan because it would buffer too much data (Scan cannot push its remaining buffered data back to the
                    // underlying BufferedReader ; probably will have to implement a custom BufferedScanner for R
                    Reader reader = con.reader(ast);
                    ArrayList<String> buf = new ArrayList<>();
                    int nread = 0;
                    StringBuilder item = null;
                    int c;
                    c = skip_whites(reader);
                    if (c != -1) {
                        for (;;) {
                            if (is_white(c)) {
                                if (item != null) {
                                    buf.add(item.toString());
                                    nread++;
                                }
                                if (nread == nmax) {
                                    break;
                                }
                                c = skip_whites(reader);
                                item = null; // TODO: get rid of allocation
                                continue;
                            }
                            if (c == -1) {
                                if (item != null) {
                                    buf.add(item.toString());
                                    nread++;
                                }
                                break;
                            }
                            if (item == null) {
                                item = new StringBuilder();
                            }
                            item.append((char) c);
                            c = reader.read();
                        }
                    }
                    RAny res = scan(buf, ast, what);
                    if (!quiet) {
                        Console.println(String.format("Read %d item%s.", nread, nread == 1 ? "" : "s"));
                    }
                    return res;
                } catch (IOException e) {
                    throw RError.getGenericError(ast, e.toString());
                } finally {
                    if (!wasOpen) {
                        con.close(ast);
                    }
                }
            }
        };
    }

    public static int parseNMax(RAny arg, ASTNode ast) {
        RInt narg = Convert.coerceToIntWarning(arg, ast);
        if (narg.size() >= 1) { return narg.getInt(0); }
        return RInt.NA;
    }

    public static boolean parseQuiet(RAny arg) {
        RLogical narg = arg.asLogical();
        if (narg.size() >= 1) { return narg.getLogical(0) == RLogical.TRUE; }
        return false;
    }

    private static ConversionStatus cs = new ConversionStatus();

    public static RString scanString(ArrayList<String> src, @SuppressWarnings("unused") ASTNode ast) {
        int size = src.size();
        String[] content = new String[size];
        return RString.RStringFactory.getFor(src.toArray(content));
    }

    public static RComplex scanComplex(ArrayList<String> src, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        double[] content = new double[2 * size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            Complex c = Convert.string2complex(str, cs);
            if (cs.naIntroduced) {
                if (str.equals("NA")) {
                    cs.naIntroduced = false;
                } else {
                    throw RError.getScanUnexpected(ast, "a complex", str);
                }
            }
            content[2 * i] = c.realValue();
            content[2 * i + 1] = c.imagValue();
        }
        return RComplex.RComplexFactory.getFor(content);
    }

    public static RDouble scanDouble(ArrayList<String> src, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        double[] content = new double[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            content[i] = Convert.string2double(str, cs);
            if (cs.naIntroduced) {
                if (str.equals("NA")) {
                    cs.naIntroduced = false;
                } else {
                    throw RError.getScanUnexpected(ast, "a real", str); }
                }
        }
        return RDouble.RDoubleFactory.getFor(content);
    }

    public static RInt scanInt(ArrayList<String> src, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        int[] content = new int[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            content[i] = Convert.string2int(str, cs);
            if (cs.naIntroduced) {
                if (str.equals("NA")) {
                    cs.naIntroduced = false;
                } else {
                    throw RError.getScanUnexpected(ast, "an integer", str);
                }
            }
        }
        return RInt.RIntFactory.getFor(content);
    }

    public static RLogical scanLogical(ArrayList<String> src, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        int[] content = new int[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);
            content[i] = Convert.string2logical(str, cs);
            if (cs.naIntroduced) {
                if (str.equals("NA")) {
                    cs.naIntroduced = false;
                } else {
                    throw RError.getScanUnexpected(ast, "a logical", str);
                }
            }
        }
        return RLogical.RLogicalFactory.getFor(content);
    }

    static int hexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }

    public static RRaw scanRaw(ArrayList<String> src, ASTNode ast) {
        int size = src.size();
        cs.naIntroduced = false;
        cs.outOfRange = false;
        byte[] content = new byte[size];
        for (int i = 0; i < size; i++) {
            String str = src.get(i);

            // conversion taken from GNU-R (yes, a bit strange)
            if (str.equals("NA")) {
                // zero
                continue;
            }
            int slen = str.length();
            if (slen == 2) {
                int a = hexDigit(str.charAt(0));
                int b = hexDigit(str.charAt(1));
                if (a != -1 && b != -1) {
                    content[i] = (byte) (a * 16 + b);
                    continue;
                }
            }
            if (slen == 1) {
                if (hexDigit(str.charAt(0)) != -1) {
                    // zero;
                    continue;
                }
            }
            throw RError.getScanUnexpected(ast, "a raw", str);
        }
        return RRaw.RRawFactory.getFor(content);
    }

    public static RAny scan(ArrayList<String> src, ASTNode ast, RAny what) {
        if (what instanceof RString) { return scanString(src, ast); }
        if (what instanceof RDouble) { return scanDouble(src, ast); }
        if (what instanceof RInt) { return scanInt(src, ast); }
        if (what instanceof RLogical) { return scanLogical(src, ast); }
        if (what instanceof RRaw) { return scanRaw(src, ast); }
        if (what instanceof RComplex) { return scanComplex(src, ast); }
        throw RError.getInvalidArgument(ast, "what");
    }

    public static boolean is_white(int c) {
        return c == '\r' || c == '\n' || c == '\t' || c == ' ';
    }

    public static int skip_whites(Reader reader) throws IOException {
        int c;
        for (;;) {
            c = reader.read();
            if (!is_white(c)) { return c;

            }
        }
    }

}

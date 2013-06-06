package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class FilePath extends CallFactory {

    static final CallFactory _ = new FilePath("file.path", new String[]{"...", "fsep"}, new String[] {});

    private FilePath(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posFsep = ia.position("fsep");

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                String sep;
                int nStringArgs;

                if (posFsep == -1) {
                    sep = "/"; // FIXME: support .Platform$file.sep, but is / always used, anyway?
                    nStringArgs = args.length;
                } else {
                    sep = Paste.parseSeparator(ast, args[posFsep]);
                    nStringArgs = args.length - 1;
                }
                RString[] stringArgs = new RString[nStringArgs];

                int j = 0; // FIXME: this code is similar to paste, if we can make paste very fast (faster than this code),
                           // perhaps we can just use paste; a common thing would be to specialize for scalar arguments
                int maxLength = 0;
                for (int i = 0; i < args.length; i++) {
                    if (i == posFsep) {
                        continue;
                    }
                    RString s = AsBase.genericAsString(ast, args[i]); // FIXME: can we remove R-level boxing?
                    stringArgs[j++] = s;
                    int ssize = s.size();
                    if (ssize > maxLength) {
                        maxLength = ssize;
                    }
                }

                String[] content = new String[maxLength];
                for (int i = 0; i < maxLength; i++) {
                    StringBuilder str = new StringBuilder();
                    for (j = 0; j < nStringArgs; j++) {
                        if (j > 0) {
                            str.append(sep);
                        }
                        RString s = stringArgs[j];
                        str.append(s.getString(i % s.size()));
                    }
                    content[i] = str.toString();
                }
                return RString.RStringFactory.getFor(content);
            }

        };
    }

}

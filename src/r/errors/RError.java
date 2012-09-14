package r.errors;

import r.nodes.*;

public abstract class RError extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public static final String LENGTH_GT_1 = "the condition has length > 1 and only the first element will be used";
    public static final String LENGTH_0 = "argument is of length zero";
    public static final String NA_UNEXP = "missing value where TRUE/FALSE needed";
    public static final String UNKNOW_VARIABLE = "object not found";
    public static final String UNUSED_ARGUMENT = "unused argument(s)";
    public static final String LENGTH_NOT_MULTI = "longer object length is not a multiple of shorter object length";
    public static final String INTEGER_OVERFLOW = "NAs produced by integer overflow";
    public static final String NA_OR_NAN = "NA/NaN argument";

    public static final String ONLY_FIRST_USED = "numerical expression has %d elements: only the first used";

    public static RError getNYI(final String msg) {
        return new RError() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return msg == null ? "Not yet implemented ..." : msg;
            }
        };
    }
    public static RError getNulLength(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return LENGTH_0;
            }
        };
    }
    public static RError getNAorNaN(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NA_OR_NAN;
            }
        };
    }

    public static RError getUnexpectedNA(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NA_UNEXP;
            }
        };
    }

    static class RErrorInExpr extends RError {
        private ASTNode errorNode;
        private static final long serialVersionUID = 1L;

        public RErrorInExpr(ASTNode node) {
            errorNode = node;
        }

        public ASTNode getErrorNode() {
            return errorNode;
        }
    }

    public static RError getUnknownVariable(ASTNode source) {
        return new RErrorInExpr(source){
            private static final long serialVersionUID = 1L;
            @Override
            public String getMessage() {
                return UNKNOW_VARIABLE;
            }
        };
    }

    public static RError getGenericError(ASTNode source, final String msg) {
        return new RErrorInExpr(source) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return msg;
            }
        };
    }
}

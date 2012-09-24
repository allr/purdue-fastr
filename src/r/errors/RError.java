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
    public static final String SUBSCRIPT_BOUNDS = "subscript out of bounds";
    public static final String SELECT_LESS_1 = "attempt to select less than one element";
    public static final String SELECT_MORE_1 = "attempt to select more than one element";
    public static final String ONLY_0_MIXED = "only 0's may be mixed with negative subscripts";
    public static final String REPLACEMENT_0 = "replacement has length zero";
    public static final String NOT_MULTIPLE_REPLACEMENT = "number of items to replace is not a multiple of replacement length";
    public static final String MORE_SUPPLIED_REPLACE = "more elements supplied than there are to replace";
    public static final String NA_SUBSCRIPTED = "NAs are not allowed in subscripted assignments";
    public static final String INVALID_ARG_TYPE = "invalid argument type";
    public static final String INVALID_ARG_TYPE_UNARY = "invalid argument to unary operator";
    public static final String INVALID_LENGTH = "invalid 'length' argument";
    public static final String VECTOR_SIZE_NEGATIVE = "vector size cannot be negative";

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

    public static RError getSubscriptBounds(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return SUBSCRIPT_BOUNDS;
            }
        };
    }

    public static RError getSelectLessThanOne(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return SELECT_LESS_1;
            }
        };
    }

    public static RError getSelectMoreThanOne(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return SELECT_MORE_1;
            }
        };
    }

    public static RError getOnlyZeroMixed(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return ONLY_0_MIXED;
            }
        };
    }

    public static RError getReplacementZero(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return REPLACEMENT_0;
            }
        };
    }

    public static RError getMoreElementsSupplied(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return MORE_SUPPLIED_REPLACE;
            }
        };
    }

    public static RError getNASubscripted(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NA_SUBSCRIPTED;
            }
        };
    }

    public static RError getInvalidArgType(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return INVALID_ARG_TYPE;
            }
        };
    }

    public static RError getInvalidArgTypeUnary(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return INVALID_ARG_TYPE_UNARY;
            }
        };
    }

    public static RError getInvalidLength(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return INVALID_LENGTH;
            }
        };
    }

    public static RError getVectorSizeNegative(ASTNode expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return VECTOR_SIZE_NEGATIVE;
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

package r.errors;

import r.nodes.*;

public abstract class RError extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public static final String LENGTH_GT_1 = "the condition has length > 1 and only the first element will be used";
    public static final String LENGHT_0 = "argument is of length zero";

    public static final String NA_UNEXP = "missing value where TRUE/FALSE needed";

    public static RError getNulLength(Node expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return LENGHT_0;
            }
        };
    }

    public static RError getUnexpectedNA(Node expr) {
        return new RErrorInExpr(expr) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getMessage() {
                return NA_UNEXP;
            }
        };
    }

    static class RErrorInExpr extends RError {
        private Node errorNode;
        private static final long serialVersionUID = 1L;

        public RErrorInExpr(Node node) {
            errorNode = node;
        }

        public Node getErrorNode() {
            return errorNode;
        }
    }
}

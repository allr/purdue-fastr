package r.builtins;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.AnalyzedArguments;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

// FIXME: this would have been easier to write in R
//        GNU R has this written in R, but the code depends on too many things we don't support yet
public class Seq {
    private static final String[] paramNames = new String[]{"from", "to", "by", "length.out", "along.with"};

    private static final int IFROM = 0;
    private static final int ITO = 1;
    private static final int IBY = 2;
    private static final int ILENGTH_OUT = 3;
    private static final int IALONG_WITH = 4;

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        return RInt.RIntFactory.getScalar(1);
                    }

                };
            }
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            // handle common cases statically
            if (provided[IFROM] && provided[ITO]) {
                  if (exprs.length == 2) {
                     // from:to
                     if (paramPositions[IFROM] == 0) {  //
                         return Colon.FACTORY.create(call, names, exprs);
                     } else {
                         RSymbol[] newNames = new RSymbol[2];
                         newNames[0] = names[1];
                         newNames[1] = names[0];
                         RNode[] newExprs = new RNode[2];
                         newExprs[0] = exprs[1];
                         newExprs[1] = exprs[0];
                         return  Colon.FACTORY.create(call, newNames, newExprs);
                     }
                  }
                  if (exprs.length == 3) {
                      if (provided[IBY]) {
                          //from, to, by
                          return new BuiltIn(call, names, exprs) {

                              // note: does not implement the full semantics
                              @Override
                              public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {

                                  RAny argfrom = args[paramPositions[IFROM]]; // FIXME: will this be optimized out?
                                  RAny argto = args[paramPositions[ITO]];
                                  RAny argby = args[paramPositions[IBY]];

                                  if (!(argfrom instanceof RArray && argto instanceof RArray && argby instanceof RArray)) {
                                      Utils.nyi("unsupported argument types");
                                  }

                                  RArray afrom = (RArray) argfrom;
                                  RArray ato = (RArray) argto;
                                  RArray aby = (RArray) argby;

                                  Colon.checkScalar(afrom, ast, context);
                                  Colon.checkScalar(ato, ast, context);
                                  Colon.checkScalar(aby, ast, context);

                                      // FIXME: perhaps could be optimized for integer-only case
                                  double from = afrom.asDouble().getDouble(0);
                                  double to = ato.asDouble().getDouble(0);
                                  double by = aby.asDouble().getDouble(0);

                                  double del = to - from;
                                  if (del == 0 && to == 0) {
                                      return argto;
                                  }
                                  double n = del / by;
                                  if (!RDouble.RDoubleUtils.isFinite(n)) {
                                      if (by == 0 && del == 0) {
                                          return argfrom;
                                      }
                                      throw RError.getInvalidTFB(ast);
                                  }
                                  if (n < 0) {
                                      throw RError.getWrongSignInBy(ast);
                                  }
                                  if (n > Integer.MAX_VALUE) {
                                      throw RError.getByTooSmall(ast);
                                  }
                                  double dd = Math.abs(del) / Math.max(Math.abs(to), Math.abs(from));
                                  if (dd < 100 * RDouble.EPSILON) {
                                      return argfrom;
                                  }
                                  if (ato instanceof RInt && afrom instanceof RInt && aby instanceof RInt) {
                                      return RInt.RIntFactory.forSequence((int) from, (int) (from + ((int) n) * by), (int) by);
                                  } else {
                                      int in = (int) (n + 1e-10);
                                      double[] content = new double[in + 1];
                                      if (by > 0) {
                                          for (int i = 0; i <= in; i++) {
                                              double x = from + i * by;
                                              if (x <= to) {
                                                  content[i] = x;
                                              } else {
                                                  content[i] = to;
                                              }
                                          }
                                      } else {
                                          for (int i = 0; i <= in; i++) {
                                              double x = from + i * by;
                                              if (x >= to) {
                                                  content[i] = x;
                                              } else {
                                                  content[i] = to;
                                              }
                                          }
                                      }
                                      return RDouble.RDoubleFactory.getForArray(content);
                                  }
                              }
                          };

                      }
                      if (provided[ILENGTH_OUT]) {
                          // from, to, length.out
                          return new BuiltIn(call, names, exprs) {

                              // note: does not implement the full semantics
                              @Override
                              public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {

                                  RAny argfrom = args[paramPositions[IFROM]]; // FIXME: will this be optimized out ?
                                  RAny argto = args[paramPositions[ITO]];
                                  RAny arglengthOut = args[paramPositions[ILENGTH_OUT]];

                                  if (!(argfrom instanceof RArray && argto instanceof RArray && arglengthOut instanceof RArray)) {
                                      Utils.nyi("unsupported argument types");
                                  }

                                  RArray afrom = (RArray) argfrom;
                                  RArray ato = (RArray) argto;
                                  RArray alengthOut = (RArray) arglengthOut;

                                  Colon.checkScalar(afrom, ast, context);
                                  Colon.checkScalar(ato, ast, context);
                                  Colon.checkScalar(alengthOut, ast, context);

                                  double from = afrom.asDouble().getDouble(0);
                                  double to = ato.asDouble().getDouble(0);
                                  double lengthOut = alengthOut.asDouble().getDouble(0);

                                  if (!RDouble.RDoubleUtils.isFinite(lengthOut) || lengthOut < 0) {
                                      throw RError.getLengthNonnegative(ast);
                                  }
                                  if (lengthOut == 0) {
                                      return RInt.EMPTY;
                                  }
                                  if (lengthOut == 1) {
                                      return argfrom;
                                  }
                                  if (lengthOut == 2) {
                                      return Combine.genericCombine(new RAny[] {argfrom, argto});
                                  }
                                  if (from == to) {
                                      return Rep.rep(ast, argfrom, arglengthOut);
                                  }

                                  double by = ((to - from) / (lengthOut - 1)); // FIXME: this may not reflect exactly R semantics in corner cases

                                  // FIXME: could do a view here
                                  int len = (int) lengthOut;
                                  RAny[] vec = new RAny[len];
                                  vec[0] = argfrom;
                                  vec[vec.length - 1] = argto;
                                  for (int i = 1; i <= len - 2; i++) {
                                      vec[i] = RDouble.RDoubleFactory.getScalar(from + i * by);
                                  }
                                  return Combine.genericCombine(vec);
                              }
                          };
                      }
                  }
            } else {
                  if (exprs.length == 1) {
                      if (provided[IALONG_WITH]) {
                          return new BuiltIn.BuiltIn1(call, names, exprs) {

                              // note: some error messages are not exactly like in R, but they are quite close
                              @Override
                              public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {

                                  if (arg instanceof RArray) {
                                      RArray aarg = (RArray) arg;
                                      int len = aarg.size();
                                      if (len == 0) {
                                          return RInt.EMPTY;
                                      }
                                      return Colon.create(1, len);
                                  }
                                  Utils.nyi();
                                  return null;
                              }
                          };
                      }
                      if (provided[IFROM]) {
                          return new BuiltIn.BuiltIn1(call, names, exprs) {

                              // note: some error messages are not exactly like in R, but they are quite close
                              @Override
                              public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {

                                  if (arg instanceof RArray) {
                                      RArray aarg = (RArray) arg;
                                      int len = aarg.size();
                                      if (len == 0) {
                                          return RInt.EMPTY;
                                      }
                                      if (len == 1) {
                                          if (aarg instanceof RInt) {
                                              int ia = ((RInt) aarg).getInt(0);
                                              Colon.checkNA(ia, ast);
                                              return Colon.create(1, ia);
                                          }
                                          if (aarg instanceof RDouble) {
                                              double da = ((RDouble) aarg).getDouble(0);
                                              Colon.checkNAandNaN(da, ast);
                                              if (RDouble.RDoubleUtils.fitsRInt(da)) {
                                                  return Colon.create(1, (int) da);
                                              } else {
                                                  return Colon.create(1.0, da);
                                              }
                                          }
                                      }
                                      return Colon.create(1, len);
                                  }
                                  Utils.nyi();
                                  return null;
                              }
                          };
                      }
                      if (provided[ILENGTH_OUT]) {
                          return new BuiltIn.BuiltIn1(call, names, exprs) {

                              // note: some error messages are not exactly like in R, but they are quite close
                              @Override
                              public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {

                                  if (arg instanceof RInt) {
                                      RInt lint = (RInt) arg;
                                      Colon.checkScalar(lint, ast, context);
                                      int li = lint.getInt(0);
                                      if (li == 0) {
                                          return RInt.EMPTY;
                                      }
                                      if (li < 0) {
                                          throw RError.getLengthNonnegative(ast);
                                      }
                                      Colon.checkNA(li, ast);
                                      return Colon.create(1, li);
                                  }
                                  if (arg instanceof RDouble) {
                                      RDouble ldbl = (RDouble) arg;
                                      Colon.checkScalar(ldbl, ast, context);
                                      double ld = ldbl.getDouble(0);
                                      if (RDouble.RDoubleUtils.isNAorNaN(ld) || ld < 0) {
                                          throw RError.getLengthNonnegative(ast);
                                      }
                                      Colon.checkNAandNaN(ld, ast);
                                      if (ld == 0) {
                                          return RInt.EMPTY;
                                      }
                                      double id = Math.ceil(ld);
                                      if (RDouble.RDoubleUtils.fitsRInt(id)) {
                                          return Colon.create(1, (int) id);
                                      } else {
                                          return Colon.create(1.0, ld);
                                      }
                                  }
                                  if (arg instanceof RLogical) {
                                      RLogical llog = (RLogical) arg;
                                      Colon.checkScalar(llog, ast, context);
                                      int ll = llog.getLogical(0);
                                      if (ll == RLogical.TRUE) {
                                          return RInt.BOXED_ONE;
                                      }
                                      if (ll == RLogical.FALSE) {
                                          return RInt.EMPTY;
                                      }
                                      throw RError.getLengthNonnegative(ast);
                                  }

                                  return RInt.RIntFactory.getScalar(1);
                              }

                          };
                      }
                  }
            }
            Utils.nyi("General case for seq to be implemented (in R?)");
            return null;
        }
    };

}

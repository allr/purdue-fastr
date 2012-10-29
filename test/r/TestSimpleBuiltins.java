package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleBuiltins extends TestBase {

    @Test
    public void testSequence() throws RecognitionException {
        assertEval("{ 5L:10L }", "5L, 6L, 7L, 8L, 9L, 10L");
        assertEval("{ 5L:(0L-5L) }", "5L, 4L, 3L, 2L, 1L, 0L, -1L, -2L, -3L, -4L, -5L");
        assertEval("{ 1:10 }", "1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L"); // note: yes, GNU R will convert to integers
        assertEval("{ 1:(0-10) }", "1L, 0L, -1L, -2L, -3L, -4L, -5L, -6L, -7L, -8L, -9L, -10L");
        assertEval("{ 1L:(0-10) }", "1L, 0L, -1L, -2L, -3L, -4L, -5L, -6L, -7L, -8L, -9L, -10L");
        assertEval("{ 1:(0L-10L) }", "1L, 0L, -1L, -2L, -3L, -4L, -5L, -6L, -7L, -8L, -9L, -10L");
        assertEval("{ (0-12):1.5 }", "-12L, -11L, -10L, -9L, -8L, -7L, -6L, -5L, -4L, -3L, -2L, -1L, 0L, 1L");
        assertEval("{ 1.5:(0-12) }", "1.5, 0.5, -0.5, -1.5, -2.5, -3.5, -4.5, -5.5, -6.5, -7.5, -8.5, -9.5, -10.5, -11.5");
        assertEval("{ (0-1.5):(0-12) }", "-1.5, -2.5, -3.5, -4.5, -5.5, -6.5, -7.5, -8.5, -9.5, -10.5, -11.5");
        assertEval("{ 10:1 }", "10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L");
        assertEval("{ (0-5):(0-9) }", "-5L, -6L, -7L, -8L, -9L");

        assertEval("{ seq(1,10) }", "1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L");
        assertEval("{ seq(10,1) }", "10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L");
        assertEval("{ seq(from=1,to=3) }", "1L, 2L, 3L");
        assertEval("{ seq(to=-1,from=-10) }", "-10L, -9L, -8L, -7L, -6L, -5L, -4L, -3L, -2L, -1L");
        assertEval("{ seq(length.out=13.4) }", "1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L");
        assertEval("{ seq(length.out=0) }", "integer(0)");
        assertEval("{ seq(length.out=1) }", "1L");
        assertEval("{ seq(along.with=10) }", "1L");
        assertEval("{ seq(along.with=NA) }", "1L");
        assertEval("{ seq(along.with=1:10) }", "1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L");
        assertEval("{ seq(along.with=-3:-5) }", "1L, 2L, 3L");
        assertEval("{ seq(from=1.4) }", "1L");
        assertEval("{ seq(from=1.7) }", "1L");
        assertEval("{ seq(from=10:12) }", "1L, 2L, 3L");
        assertEval("{ seq(from=c(TRUE, FALSE)) }", "1L, 2L");
        assertEval("{ seq(from=TRUE, to=TRUE, length.out=0) }", "integer(0)");
        assertEval("{ seq(from=10.5, to=15.4, length.out=4) }", "10.5, 12.133333333333333, 13.766666666666667, 15.4");
        assertEval("{ seq(from=11, to=12, length.out=2) }", "11.0, 12.0");
        assertEval("{ seq(from=1,to=3,by=1) }", "1.0, 2.0, 3.0");
        assertEval("{ seq(from=-10,to=-5,by=2) }", "-10.0, -8.0, -6.0");
        assertEval("{ seq(from=-10.4,to=-5.8,by=2.1) }", "-10.4, -8.3, -6.2");
        assertEval("{ seq(from=3L,to=-2L,by=-4.2) }", "3.0, -1.2000000000000002");
    }

    @Test
    public void testArrayConstructors() throws RecognitionException {
        assertEval("{ integer() }", "integer(0)");
        assertEval("{ double() }", "numeric(0)");
        assertEval("{ logical() }", "logical(0)");
        assertEval("{ double(3) }", "0.0, 0.0, 0.0");
        assertEval("{ logical(3L) }", "FALSE, FALSE, FALSE");
    }

    @Test
    public void testMaximum() throws RecognitionException {
        assertEval("{ max((-1):100) }", "100L");
        assertEval("{ max(1:10, 100:200, c(4.0, 5.0)) }", "200.0");
        assertEval("{ max(1:10, 100:200, c(4.0, 5.0), c(TRUE,FALSE,NA)) }", "NA");
    }

    @Test
    public void testRep() throws RecognitionException {
        assertEval("{ rep(1,3) }", "1.0, 1.0, 1.0");
        assertEval("{ rep(1:3,2) }", "1L, 2L, 3L, 1L, 2L, 3L");
        assertEval("{ rep(c(1,2),0) }", "numeric(0)");
    }

    @Test
    public void testCombine() throws RecognitionException {
        assertEval("{ c(1.0,1L) }", "1.0, 1.0");
        assertEval("{ c(1L,1.0) }", "1.0, 1.0");
        assertEval("{ c(TRUE,1L,1.0,list(3,4)) }", "[[1]]\nTRUE\n\n[[2]]\n1L\n\n[[3]]\n1.0\n\n[[4]]\n3.0\n\n[[5]]\n4.0");
        assertEval("{ c(TRUE,1L,1.0,list(3,list(4,5))) }", "[[1]]\nTRUE\n\n[[2]]\n1L\n\n[[3]]\n1.0\n\n[[4]]\n3.0\n\n[[5]]\n[[5]][[1]]\n4.0\n\n[[5]][[2]]\n5.0");
        assertEval("{ c() }", "NULL");
        assertEval("{ c(NULL,NULL) }", "NULL");
        assertEval("{ c(NULL,1,2,3) }", "1.0, 2.0, 3.0");
    }

    @Test
    public void testIsNA() throws RecognitionException {
        assertEval("{ is.na(c(1,2,3,4)) }", "FALSE, FALSE, FALSE, FALSE");
        assertEval("{ is.na(1[10]) }", "TRUE");
        assertEval("{ is.na(c(1[10],2[10],3)) }", "TRUE, TRUE, FALSE");
        assertEval("{ is.na(list(1[10],1L[10],list(),integer())) }", "TRUE, TRUE, FALSE, FALSE");
    }

    @Test
    public void testCasts() throws RecognitionException {
        assertEval("{ as.integer(c(1,2,3)) }", "1L, 2L, 3L");
        assertEval("{ as.integer(list(c(1),2,3)) }", "1L, 2L, 3L");
        assertEval("{ as.integer(list(integer(),2,3)) }", "NA, 2L, 3L");
        assertEval("{ as.integer(list(list(1),2,3)) }", "NA, 2L, 3L");
    }

    @Test
    public void testSum() throws RecognitionException {
        assertEval("{ sum(1:6, 3, 4) }", "28.0");
        assertEval("{ sum(1:6, 3L, TRUE) }", "25L");
        assertEval("{ sum() }", "0L");
        assertEval("{ sum(0, 1[3]) }", "NA");
        assertEval("{ sum(na.rm=FALSE, 0, 1[3]) }", "NA");
        assertEval("{ sum(0, na.rm=FALSE, 1[3]) }", "NA");
        assertEval("{ sum(0, 1[3], na.rm=FALSE) }", "NA");
        assertEval("{ sum(0, 1[3], na.rm=TRUE) }", "0.0");
    }

    @Test
    public void testApply() throws RecognitionException {
        assertEval("{ lapply(1:3, function(x) { 2*x }) }", "[[1]]\n2.0\n\n[[2]]\n4.0\n\n[[3]]\n6.0");
        assertEval("{ lapply(1:3, function(x,y) { x*y }, 2) }", "[[1]]\n2.0\n\n[[2]]\n4.0\n\n[[3]]\n6.0");

        assertEval("{ sapply(1:3,function(x){x*2}) }", "2.0, 4.0, 6.0");
        assertEval("{ sapply(1:3, function(x) { if (x==1) { 1 } else if (x==2) { integer() } else { TRUE } }) }", "[[1]]\n1.0\n\n[[2]]\ninteger(0)\n\n[[3]]\nTRUE");
        assertEval("{ f<-function(g) { sapply(1:3, g) } ; f(function(x) { x*2 }) }", "2.0, 4.0, 6.0");
        assertEval("{ f<-function(g) { sapply(1:3, g) } ; f(function(x) { x*2 }) ; f(function(x) { TRUE }) }", "TRUE, TRUE, TRUE");
        assertEval("{ sapply(1:3, function(x) { if (x==1) { list(1) } else if (x==2) { list(NULL) } else { list(2) } }) }", "[[1]]\n1.0\n\n[[2]]\nNULL\n\n[[3]]\n2.0");
        assertEval("{ sapply(1:3, function(x) { if (x==1) { list(1) } else if (x==2) { list(NULL) } else { list() } }) }", "[[1]]\n[[1]][[1]]\n1.0\n\n[[2]]\n[[2]][[1]]\nNULL\n\n[[3]]\nlist()");
    }

    @Test
    public void testOther() throws RecognitionException {
        assertEval("{ rev.mine <- function(x) { if (length(x)) x[length(x):1L] else x } ; rev.mine(1:3) }", "3L, 2L, 1L");
    }
}

package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleVectors extends TestBase {

    @Test
    public void testScalarIndex() throws RecognitionException {
        assertEval("{ x<-1:10; x[3] }", "3L");
        assertEval("{ x<-1:10; x[3L] }", "3L");
        assertEval("{ x<-c(1,2,3); x[3] }", "3.0");
        assertEval("{ x<-c(1,2,3); x[3L] }", "3.0");
        assertEval("{ x<-1:3; x[0-2] }", "1L, 3L");
        assertEval("{ x<-1:3; x[FALSE] }", "integer(0)");
        assertEval("{ x<-1:3; x[TRUE] }", "1L, 2L, 3L");
        assertEval("{ x<-c(TRUE,TRUE,FALSE); x[0-2] }", "TRUE, FALSE");
        assertEval("{ x<-c(1,2);x[[0-1]] }", "2.0");
        assertEval("{ x<-c(1,2);x[0-3] }", "1.0, 2.0");
        assertEval("{ x<-10; x[0-1] }", "numeric(0)");
        assertEval("{ x<-10; x[NA] }", "NA");
    }

    @Test
    public void testVectorIndex() throws RecognitionException {
        assertEval("{ x<-1:5 ; x[3:4] }", "3L, 4L");
        assertEval("{ x<-1:5 ; x[4:3] }", "4L, 3L");
        assertEval("{ x<-c(1,2,3,4,5) ; x[4:3] }", "4.0, 3.0");
        assertEval("{ (1:5)[3:4] }", "3L, 4L");
        assertEval("{ x<-(1:5)[2:4] ; x[2:1] }", "3L, 2L");
        assertEval("{ x<-1:5;x[c(0-2,0-3)] }", "1L, 4L, 5L");
        assertEval("{ x<-1:5;x[c(0-2,0-3,0,0,0)] }", "1L, 4L, 5L");
        assertEval("{ x<-1:5;x[c(2,5,4,3,3,3,0)] }", "2L, 5L, 4L, 3L, 3L, 3L");
        assertEval("{ x<-1:5;x[c(2L,5L,4L,3L,3L,3L,0L)] }", "2L, 5L, 4L, 3L, 3L, 3L");
        assertEval("{ f<-function(x, i) { x[i] } ; f(1:3,3:1) ; f(1:5,c(0,0,0,0-2)) }", "1L, 3L, 4L, 5L");
        assertEval("{ f<-function(x, i) { x[i] } ; f(1:3,0-3) ; f(1:5,c(0,0,0,0-2)) }", "1L, 3L, 4L, 5L");
        assertEval("{ f<-function(x, i) { x[i] } ; f(1:3,0L-3L) ; f(1:5,c(0,0,0,0-2)) }", "1L, 3L, 4L, 5L");
        assertEval("{ x<-1:5 ; x[c(TRUE,FALSE)] }", "1L, 3L, 5L");
        assertEval("{ x<-1:5 ; x[c(TRUE,TRUE,TRUE,NA)] }", "1L, 2L, 3L, NA, 5L");
        assertEval("{ x<-1:5 ; x[c(TRUE,TRUE,TRUE,FALSE,FALSE,FALSE,FALSE,TRUE,NA)] }", "1L, 2L, 3L, NA, NA");
        assertEval("{ f<-function(i) { x<-1:5 ; x[i] } ; f(1) ; f(1L) ; f(TRUE) }", "1L, 2L, 3L, 4L, 5L");
        assertEval("{ f<-function(i) { x<-1:5 ; x[i] } ; f(1) ; f(TRUE) ; f(1L)  }", "1L");
        assertEval("{ f<-function(i) { x<-1:5 ; x[i] } ; f(1) ; f(TRUE) ; f(c(3,2))  }", "3L, 2L");
        assertEval("{ f<-function(i) { x<-1:5 ; x[i] } ; f(1)  ; f(3:4) }", "3L, 4L");
        assertEval("{ f<-function(i) { x<-1:5 ; x[i] } ; f(c(TRUE,FALSE))  ; f(3:4) }", "3L, 4L");
    }

    @Test
    public void testScalarUpdate() throws RecognitionException {
        assertEval("{ x<-1:3; x[1]<-100L; x }", "100L, 2L, 3L");
        assertEval("{ x<-c(1,2,3); x[2L]<-100L; x }", "1.0, 100.0, 3.0");
        assertEval("{ x<-c(1,2,3); x[2L]<-100; x }", "1.0, 100.0, 3.0");
        assertEval("{ x<-c(1,2,3); x[2]<-FALSE; x }", "1.0, 0.0, 3.0");
        assertEval("{ x<-1:5; x[2]<-1000; x[3] <- TRUE; x[8]<-3L; x }", "1.0, 1000.0, 1.0, 4.0, 5.0, NA, NA, 3.0");
        assertEval("{ x<-5:1; x[0-2]<-1000; x }", "1000.0, 4.0, 1000.0, 1000.0, 1000.0");
        assertEval("{ x<-c(); x[[TRUE]] <- 2; x }", "2.0");
        assertEval("{ x<-1:2; x[[0-2]]<-100; x }", "100.0, 2.0");
        assertEval("{ f<-function(x,i,v) { x<-1:5; x[i]<-v; x} ; f(c(1L,2L),1,3L) ; f(c(1L,2L),2,3) }", "1.0, 3.0, 3.0, 4.0, 5.0");
        assertEval("{ f<-function(x,i,v) { x<-1:5; x[i]<-v; x} ; f(c(1L,2L),1,3L) ; f(c(1L,2L),8,3L) }", "1L, 2L, 3L, 4L, 5L, NA, NA, 3L");
        assertEval("{ f<-function(x,i,v) { x<-1:5; x[i]<-v; x} ; f(c(1L,2L),1,FALSE) ; f(c(1L,2L),2,3) }", "1.0, 3.0, 3.0, 4.0, 5.0");
        assertEval("{ f<-function(x,i,v) { x<-1:5; x[i]<-v; x} ; f(c(1L,2L),1,FALSE) ; f(c(1L,2L),8,TRUE) }", "1L, 2L, 3L, 4L, 5L, NA, NA, 1L");
    }

    @Test
    public void testVectorUpdate() throws RecognitionException {
        assertEval("{ x<-c(1,2,3,4,5); x[3:4]<-c(300L,400L); x }", "1.0, 2.0, 300.0, 400.0, 5.0");
        assertEval("{ x<-c(1,2,3,4,5); x[4:3]<-c(300L,400L); x }", "1.0, 2.0, 400.0, 300.0, 5.0");
        assertEval("{ x<-1:5; x[4:3]<-c(300L,400L); x }", "1L, 2L, 400L, 300L, 5L");
        assertEval("{ x<-5:1; x[3:4]<-c(300L,400L); x }", "5L, 4L, 300L, 400L, 1L");
        assertEval("{ x<-5:1; x[3:4]<-c(300,400); x }", "5.0, 4.0, 300.0, 400.0, 1.0");
        assertEval("{ x<-1:5; x[c(0-2,0-3,0-3,0-100,0)]<-256; x }", "256.0, 2.0, 3.0, 256.0, 256.0");
        assertEval("{ x<-1:5; x[c(4,2,3)]<-c(256L,257L,258L); x }", "1L, 257L, 258L, 256L, 5L");
        assertEval("{ x<-c(1,2,3,4,5); x[c(TRUE,FALSE)] <- 1000; x }", "1000.0, 2.0, 1000.0, 4.0, 1000.0");
        assertEval("{ x<-c(1,2,3,4,5,6); x[c(TRUE,TRUE,FALSE)] <- c(1000L,2000L) ; x }", "1000.0, 2000.0, 3.0, 1000.0, 2000.0, 6.0");
        assertEval("{ x<-c(1,2,3,4,5); x[c(TRUE,FALSE,TRUE,TRUE,FALSE)] <- c(1000,2000,3000); x }", "1000.0, 2.0, 2000.0, 3000.0, 5.0");
        assertEval("{ x<-c(1,2,3,4,5); x[c(TRUE,FALSE,TRUE,TRUE,0)] <- c(1000,2000,3000); x }", "3000.0, 2.0, 3.0, 4.0, 5.0");
        assertEval("{ x<-1:3; x[c(TRUE, FALSE, TRUE)] <- c(TRUE,FALSE); x }", "1L, 2L, 0L");
        assertEval("{ x<-c(TRUE,TRUE,FALSE); x[c(TRUE, FALSE, TRUE)] <- c(FALSE,TRUE); x }", "FALSE, TRUE, TRUE");
        assertEval("{ x<-c(TRUE,TRUE,FALSE); x[c(TRUE, FALSE, TRUE)] <- c(1000,2000); x }", "1000.0, 1.0, 2000.0");
        assertEval("{ x<-11:9 ; x[c(TRUE, FALSE, TRUE)] <- c(1000,2000); x }", "1000.0, 10.0, 2000.0");
        assertEval("{ f<-function(i,v) { x<-1:5 ; x[i]<-v ; x } ; f(1,1) ; f(1L,TRUE) ; f(2,TRUE) }", "1L, 1L, 3L, 4L, 5L");
        assertEval("{ f<-function(i,v) { x<-1:5 ; x[[i]]<-v ; x } ; f(1,1) ; f(1L,TRUE) ; f(2,TRUE) }", "1L, 1L, 3L, 4L, 5L");
        assertEval("{ f<-function(i,v) { x<-1:5 ; x[i]<-v ; x } ; f(3:2,1) ; f(1L,TRUE) ; f(2:4,4:2) }", "1L, 4L, 3L, 2L, 5L");
        assertEval("{ f<-function(i,v) { x<-1:5 ; x[i]<-v ; x } ; f(c(3,2),1) ; f(1L,TRUE) ; f(2:4,c(4,3,2)) }", "1.0, 4.0, 3.0, 2.0, 5.0");
        assertEval("{ f<-function(b,i,v) { b[i]<-v ; b } ; f(1:4,4:1,TRUE) ; f(c(3,2,1),8,10) }", "3.0, 2.0, 1.0, NA, NA, NA, NA, 10.0");
        assertEval("{ f<-function(b,i,v) { b[i]<-v ; b } ; f(1:4,4:1,TRUE) ; f(c(3,2,1),8,10) ; f(c(TRUE,FALSE),TRUE,FALSE) }", "FALSE, FALSE");
        assertEval("{ x<-c(TRUE,TRUE,FALSE,TRUE) ; x[3:2] <- TRUE; x }", "TRUE, TRUE, TRUE, TRUE");

        assertEval("{ x<-1:3 ; y<-(x[2]<-100) ; y }", "100.0");
        assertEval("{ x<-1:5 ; x[x[4]<-2] <- (x[4]<-100) ; x }", "1.0, 100.0, 3.0, 2.0, 5.0");
        assertEval("{ x<-1:5 ; x[3] <- (x[4]<-100) ; x }", "1.0, 2.0, 100.0, 100.0, 5.0");
        assertEval("{ x<-5:1 ; x[x[2]<-2] }", "4L");
        assertEval("{ x<-5:1 ; x[x[2]<-2] <- (x[3]<-50) ; x }", "5.0, 50.0, 50.0, 2.0, 1.0");
    }

    @Test
    public void testListDefinitions() throws RecognitionException {
        assertEval("{ list(1:4) }", "[[1]]\n1L, 2L, 3L, 4L");
        assertEval("{ list(1,list(2,list(3,4))) }", "[[1]]\n1.0\n\n[[2]]\n[[2]][[1]]\n2.0\n\n[[2]][[2]]\n[[2]][[2]][[1]]\n3.0\n\n[[2]][[2]][[2]]\n4.0");
    }

    @Test
    public void testListAccess() throws RecognitionException {
        // indexing
        assertEval("{ l<-list(1,2L,TRUE) ; l[[2]] }", "2L");
        assertEval("{ l<-list(1,2L,TRUE) ; l[c(FALSE,FALSE,TRUE)] }", "[[1]]\nTRUE");
        assertEval("{ l<-list(1,2L,TRUE) ; l[FALSE] }", "list()");
        assertEval("{ l<-list(1,2L,TRUE) ; l[-2] }", "[[1]]\n1.0\n\n[[2]]\nTRUE");
        assertEval("{ l<-list(1,2L,TRUE) ; l[NA] }", "[[1]]\nNULL\n\n[[2]]\nNULL\n\n[[3]]\nNULL");
        assertEval("{ l<-list(1,2,3) ; l[c(1,2)] }", "[[1]]\n1.0\n\n[[2]]\n2.0");
        assertEval("{ l<-list(1,2,3) ; l[c(2)] }", "[[1]]\n2.0");
        assertEval("{ x<-list(1,2L,TRUE,FALSE,5) ; x[2:4] }", "[[1]]\n2L\n\n[[2]]\nTRUE\n\n[[3]]\nFALSE");
        assertEval("{ x<-list(1,2L,TRUE,FALSE,5) ; x[4:2] }", "[[1]]\nFALSE\n\n[[2]]\nTRUE\n\n[[3]]\n2L");
        assertEval("{ x<-list(1,2L,TRUE,FALSE,5) ; x[c(-2,-3)] }", "[[1]]\n1.0\n\n[[2]]\nFALSE\n\n[[3]]\n5.0");
        assertEval("{ x<-list(1,2L,TRUE,FALSE,5) ; x[c(-2,-3,-4,0,0,0)] }", "[[1]]\n1.0\n\n[[2]]\n5.0");
        assertEval("{ x<-list(1,2L,TRUE,FALSE,5) ; x[c(2,5,4,3,3,3,0)] }", "[[1]]\n2L\n\n[[2]]\n5.0\n\n[[3]]\nFALSE\n\n[[4]]\nTRUE\n\n[[5]]\nTRUE\n\n[[6]]\nTRUE");
        assertEval("{ x<-list(1,2L,TRUE,FALSE,5) ; x[c(2L,5L,4L,3L,3L,3L,0L)] }", "[[1]]\n2L\n\n[[2]]\n5.0\n\n[[3]]\nFALSE\n\n[[4]]\nTRUE\n\n[[5]]\nTRUE\n\n[[6]]\nTRUE");
        assertEval("{ m<-list(1,2) ; m[NULL] }", "list()");

        // indexing with rewriting
        assertEval("{ f<-function(x, i) { x[i] } ; f(list(1,2,3),3:1) ; f(list(1L,2L,3L,4L,5L),c(0,0,0,0-2)) }", "[[1]]\n1L\n\n[[2]]\n3L\n\n[[3]]\n4L\n\n[[4]]\n5L");
        assertEval("{ x<-list(1,2,3,4,5) ; x[c(TRUE,TRUE,TRUE,FALSE,FALSE,FALSE,FALSE,TRUE,NA)] }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n3.0\n\n[[4]]\nNULL\n\n[[5]]\nNULL");
        assertEval("{ f<-function(i) { x<-list(1,2,3,4,5) ; x[i] } ; f(1) ; f(1L) ; f(TRUE) }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n3.0\n\n[[4]]\n4.0\n\n[[5]]\n5.0");
        assertEval("{ f<-function(i) { x<-list(1,2,3,4,5) ; x[i] } ; f(1) ; f(TRUE) ; f(1L)  }", "[[1]]\n1.0");
        assertEval("{ f<-function(i) { x<-list(1L,2L,3L,4L,5L) ; x[i] } ; f(1) ; f(TRUE) ; f(c(3,2))  }", "[[1]]\n3L\n\n[[2]]\n2L");
        assertEval("{ f<-function(i) { x<-list(1,2,3,4,5) ; x[i] } ; f(1)  ; f(3:4) }", "[[1]]\n3.0\n\n[[2]]\n4.0");
        assertEval("{ f<-function(i) { x<-list(1,2,3,4,5) ; x[i] } ; f(c(TRUE,FALSE))  ; f(3:4) }", "[[1]]\n3.0\n\n[[2]]\n4.0");

        // recursive indexing
        assertEval("{ l<-(list(list(1,2),list(3,4))); l[[c(1,2)]] }", "2.0");
        assertEval("{ l<-(list(list(1,2),list(3,4))); l[[c(1,-2)]] }", "1.0");
        assertEval("{ l<-(list(list(1,2),list(3,4))); l[[c(1,-1)]] }", "2.0");
        assertEval("{ l<-(list(list(1,2),list(3,4))); l[[c(1,TRUE)]] }", "1.0");
        assertEval("{ l<-(list(list(1,2),c(3,4))); l[[c(2,1)]] }", "3.0");
    }

    @Test
    public void testListUpdate() throws RecognitionException {
        assertEval("{ l<-list(1,2L,TRUE) ; l[[2]]<-100 ; l }", "[[1]]\n1.0\n\n[[2]]\n100.0\n\n[[3]]\nTRUE");
        assertEval("{ l<-list(1,2L,TRUE) ; l[[5]]<-100 ; l }", "[[1]]\n1.0\n\n[[2]]\n2L\n\n[[3]]\nTRUE\n\n[[4]]\nNULL\n\n[[5]]\n100.0");
        assertEval("{ l<-list(1,2L,TRUE) ; l[[3]]<-list(100) ; l }", "[[1]]\n1.0\n\n[[2]]\n2L\n\n[[3]]\n[[3]][[1]]\n100.0");

        // element deletion
        assertEval("{ m<-list(1,2) ; m[TRUE] <- NULL ; m }", "list()");
        assertEval("{ m<-list(1,2) ; m[[TRUE]] <- NULL ; m }", "[[1]]\n2.0");
        assertEval("{ m<-list(1,2) ; m[[1]] <- NULL ; m }", "[[1]]\n2.0");
        assertEval("{ m<-list(1,2) ; m[[-1]] <- NULL ; m }", "[[1]]\n1.0");
        assertEval("{ m<-list(1,2) ; m[[-2]] <- NULL ; m }", "[[1]]\n2.0");
    }
}

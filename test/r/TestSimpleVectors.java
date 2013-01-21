package r;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

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

        assertEval("{ x <- c(a=1, b=2, c=3) ; x[2] }", "  b\n2.0");
        assertEval("{ x <- c(a=1, b=2, c=3) ; x[[2]] }", "2.0");
        assertEval("{ x <- c(a=\"A\", b=\"B\", c=\"C\") ; x[-2] }", "  a   c\n\"A\" \"C\"");
        assertEval("{ x <- c(a=1+2i, b=2+3i, c=3) ; x[-2] }", "       a        c\n1.0+2.0i 3.0+0.0i");
        assertEval("{ x <- c(a=1, b=2, c=3) ; x[-2] }", "  a   c\n1.0 3.0");
        assertEval("{ x <- c(a=1L, b=2L, c=3L) ; x[-2] }", " a  c\n1L 3L");
        assertEval("{ x <- c(a=TRUE, b=FALSE, c=NA) ; x[-2] }", "   a  c\nTRUE NA");
        assertEval("{ x <- c(a=as.raw(10), b=as.raw(11), c=as.raw(12)) ; x[-2] }", " a  c\n0a 0c");

        assertEval("{ x <- c(a=1L, b=2L, c=3L) ; x[0] }", "named integer(0)");
        assertEval("{ x <- c(a=1L, b=2L, c=3L) ; x[10] }", "<NA>\n  NA");
        assertEval("{ x <- c(a=TRUE, b=FALSE, c=NA) ; x[0] }", "named logical(0)");
        assertEval("{ x <- c(TRUE, FALSE, NA) ; x[0] }", "logical(0)");
        assertEval("{ x <- list(1L, 2L, 3L) ; x[10] }", "[[1]]\nNULL");
        assertEval("{ x <- list(a=1L, b=2L, c=3L) ; x[0] }", "named list()");
        assertEval("{ x <- c(a=\"A\", b=\"B\", c=\"C\") ; x[10] }", "<NA>\n  NA");
        assertEval("{ x <- c(a=\"A\", b=\"B\", c=\"C\") ; x[0] }", "named character(0)");
        assertEval("{ x <- c(a=1+1i, b=2+2i, c=3+3i) ; x[10] }", "<NA>\n  NA");
        assertEval("{ x <- c(a=1+1i, b=2+2i, c=3+3i) ; x[0] }", "named complex(0)");
        assertEval("{ x <- c(a=as.raw(10), b=as.raw(11), c=as.raw(12)) ; x[10] }", "<NA>\n  00");
        assertEval("{ x <- c(a=as.raw(10), b=as.raw(11), c=as.raw(12)) ; x[0] }", "named raw(0)");
        assertEval("{ x <- c(a=1, b=2, c=3) ; x[10] }", "<NA>\n  NA");
        assertEval("{ x <- c(a=1, b=2, c=3) ; x[0] }", "named numeric(0)");
        assertEval("{ x <- c(a=1,b=2,c=3,d=4) ; x[\"b\"] }", "  b\n2.0");
        assertEval("{ x <- c(a=1,b=2,c=3,d=4) ; x[\"d\"] }", "  d\n4.0");
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
        assertEval("{ x<-as.complex(c(1,2,3,4)) ; x[2:4] }", "2.0+0.0i, 3.0+0.0i, 4.0+0.0i");
        assertEval("{ x<-as.raw(c(1,2,3,4)) ; x[2:4] }", "02, 03, 04");

        assertEval("{ x<-c(1,2,3,4) ; names(x) <- c(\"a\",\"b\",\"c\",\"d\") ; x[c(10,2,3,0)] }", "<NA>   b   c\n  NA 2.0 3.0");
        assertEval("{ x<-c(1,2,3,4) ; names(x) <- c(\"a\",\"b\",\"c\",\"d\") ; x[c(10,2,3)] }", "<NA>   b   c\n  NA 2.0 3.0");
        assertEval("{ x<-c(1,2,3,4) ; names(x) <- c(\"a\",\"b\",\"c\",\"d\") ; x[c(-2,-4,0)] }", "  a   c\n1.0 3.0");
        assertEval("{ x<-c(1,2) ; names(x) <- c(\"a\",\"b\") ; x[c(FALSE,TRUE,NA,FALSE)] }", "  b <NA>\n2.0   NA");
        assertEval("{ x<-c(1,2) ; names(x) <- c(\"a\",\"b\") ; x[c(FALSE,TRUE)] }", "  b\n2.0");

        assertEval("{ x <- c(a=1,b=2,c=3,d=4) ; x[character()] }", "named numeric(0)");
        assertEval("{ x <- c(a=1,b=2,c=3,d=4) ; x[c(\"b\",\"b\",\"d\",\"a\",\"a\")] }", "  b   b   d   a   a\n2.0 2.0 4.0 1.0 1.0");
        assertEval("{ x <- c(a=as.raw(10),b=as.raw(11),c=as.raw(12),d=as.raw(13)) ; f <- function(s) { x[s] } ; f(TRUE) ; f(1L) ; f(as.character(NA)) }", "<NA>\n  00");
        assertEval("{ x <- c(a=1,b=2,c=3,d=4) ; f <- function(s) { x[s] } ; f(TRUE) ; f(1L) ; f(\"b\") }", "  b\n2.0");
        assertEval("{ x <- c(a=as.raw(10),b=as.raw(11),c=as.raw(12),d=as.raw(13)) ; f <- function(s) { x[c(s,s)] } ; f(TRUE) ; f(1L) ; f(as.character(NA)) }", "<NA> <NA>\n  00   00");
        assertEval("{ x <- c(a=1,b=2,c=3,d=4) ; f <- function(s) { x[c(s,s)] } ; f(TRUE) ; f(1L) ; f(\"b\") }", "  b   b\n2.0 2.0");
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

        assertEval("{ a <- c(1L,2L,3L); a <- 1:5; a[3] <- TRUE; a }", "1L, 2L, 1L, 4L, 5L");
        assertEval("{ x <- 1:3 ; x[2] <- \"hi\"; x }", "\"1L\", \"hi\", \"3L\"");
        assertEval("{ x <- c(1,2,3) ; x[2] <- \"hi\"; x }", "\"1.0\", \"hi\", \"3.0\"");
        assertEval("{ x <- c(TRUE,FALSE,FALSE) ; x[2] <- \"hi\"; x }", "\"TRUE\", \"hi\", \"FALSE\"");
    }

    @Test
    public void testVectorUpdate() throws RecognitionException {
        assertEval("{ a <- c(1,2,3) ; b <- a; a[1] <- 4L; a }", "4.0, 2.0, 3.0");
        assertEval("{ a <- c(1,2,3) ; b <- a; a[2] <- 4L; a }", "1.0, 4.0, 3.0");
        assertEval("{ a <- c(1,2,3) ; b <- a; a[3] <- 4L; a }", "1.0, 2.0, 4.0");
        // logical value inserted to double vector
        assertEval("{ a <- c(2.1,2.2,2.3); b <- a; a[[1]] <- TRUE; a }", "1.0, 2.2, 2.3");
        assertEval("{ a <- c(2.1,2.2,2.3); b <- a; a[[2]] <- TRUE; a }", "2.1, 1.0, 2.3");
        assertEval("{ a <- c(2.1,2.2,2.3); b <- a; a[[3]] <- TRUE; a }", "2.1, 2.2, 1.0");
        // logical value inserted into logical vector
        assertEval("{ a <- c(TRUE,TRUE,TRUE); b <- a; a[[1]] <- FALSE; a }", "FALSE, TRUE, TRUE");
        assertEval("{ a <- c(TRUE,TRUE,TRUE); b <- a; a[[2]] <- FALSE; a }", "TRUE, FALSE, TRUE");
        assertEval("{ a <- c(TRUE,TRUE,TRUE); b <- a; a[[3]] <- FALSE; a }", "TRUE, TRUE, FALSE");
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
        assertEval("{ l <- double() ; l[c(TRUE,TRUE)] <-2 ; l}", "2.0, 2.0");
        assertEval("{ l <- double() ; l[c(FALSE,TRUE)] <-2 ; l}", "NA, 2.0");

        assertEval("{ a<- c('a','b','c','d'); a[3:4] <- c(4,5); a}", "\"a\", \"b\", \"4.0\", \"5.0\"");
        assertEval("{ a<- c('a','b','c','d'); a[3:4] <- c(4L,5L); a}", "\"a\", \"b\", \"4L\", \"5L\"");
        assertEval("{ a<- c('a','b','c','d'); a[3:4] <- c(TRUE,FALSE); a}", "\"a\", \"b\", \"TRUE\", \"FALSE\"");


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

        assertEval("{ v<-1:3 ; v[TRUE] <- 100 ; v }", "100.0, 100.0, 100.0");
        assertEval("{ v<-1:3 ; v[-1] <- c(100,101) ; v }", "1.0, 100.0, 101.0");
        assertEval("{ v<-1:3 ; v[TRUE] <- c(100,101,102) ; v }", "100.0, 101.0, 102.0");

        assertEval("{ x <- c(a=1,b=2,c=3) ; x[2]<-10; x }", "  a    b   c\n1.0 10.0 3.0");
        assertEval("{ x <- c(a=1,b=2,c=3) ; x[2:3]<-10; x }", "  a    b    c\n1.0 10.0 10.0");
        assertEval("{ x <- c(a=1,b=2,c=3) ; x[c(2,3)]<-10; x }", "  a    b    c\n1.0 10.0 10.0");
        assertEval("{ x <- c(a=1,b=2,c=3) ; x[c(TRUE,TRUE,FALSE)]<-10; x }", "   a    b   c\n10.0 10.0 3.0");
        assertEval("{ x <- c(a=1,b=2) ; x[2:3]<-10; x }", "  a    b     \n1.0 10.0 10.0");
        assertEval("{ x <- c(a=1,b=2) ; x[c(2,3)]<-10; x }", "  a    b     \n1.0 10.0 10.0");
        assertEval("{ x <- c(a=1,b=2) ; x[3]<-10; x }", "  a   b     \n1.0 2.0 10.0");
        assertEval("{ x <- matrix(1:2) ; x[c(FALSE,FALSE,TRUE)]<-10; x }", "1.0, 2.0, 10.0");
        assertEval("{ x <- 1:2 ; x[c(FALSE,FALSE,TRUE)]<-10; x }", "1.0, 2.0, 10.0");
        assertEval("{ x <- c(a=1,b=2) ; x[c(FALSE,FALSE,TRUE)]<-10; x }", "  a   b     \n1.0 2.0 10.0");

        assertEval("{ x<-c(a=1,b=2,c=3) ; x[[\"b\"]]<-200; x }", "  a     b   c\n1.0 200.0 3.0");
        assertEval("{ x<-c(a=1,b=2,c=3) ; x[[\"d\"]]<-200; x }", "  a   b   c     d\n1.0 2.0 3.0 200.0");
        assertEval("{ x<-c() ; x[c(\"a\",\"b\",\"c\",\"d\")]<-c(1,2); x }", "  a   b   c   d\n1.0 2.0 1.0 2.0");
        assertEval("{ x<-c(a=1,b=2,c=3) ; x[\"d\"]<-4 ; x }", "  a   b   c   d\n1.0 2.0 3.0 4.0");
        assertEval("{ x<-c(a=1,b=2,c=3) ; x[c(\"d\",\"e\")]<-c(4,5) ; x }", "  a   b   c   d   e\n1.0 2.0 3.0 4.0 5.0");
        assertEval("{ x<-c(a=1,b=2,c=3) ; x[c(\"d\",\"a\",\"d\",\"a\")]<-c(4,5) ; x }", "  a   b   c   d\n5.0 2.0 3.0 4.0");

        assertEval("{ x <- c(TRUE,TRUE,TRUE,TRUE); x[2:3] <- c(FALSE,FALSE); x }", "TRUE, FALSE, FALSE, TRUE");
        assertEval("{ x <- c(TRUE,TRUE,TRUE,TRUE); x[3:2] <- c(FALSE,TRUE); x }", "TRUE, TRUE, FALSE, TRUE");

        assertEval("{ x <- c('a','b','c','d'); x[2:3] <- 'x'; x}", "\"a\", \"x\", \"x\", \"d\"");
        assertEval("{ x <- c('a','b','c','d'); x[2:3] <- c('x','y'); x}", "\"a\", \"x\", \"y\", \"d\"");
        assertEval("{ x <- c('a','b','c','d'); x[3:2] <- c('x','y'); x}", "\"a\", \"y\", \"x\", \"d\"");

        assertEval("{ x <- c('a','b','c','d'); x[c(TRUE,FALSE,TRUE)] <- c('x','y','z'); x }", "\"x\", \"b\", \"y\", \"z\"");

        assertEval("{ x <- c(TRUE,TRUE,TRUE,TRUE); x[c(TRUE,TRUE,FALSE)] <- c(10L,20L,30L); x }", "10L, 20L, 1L, 30L");
        assertEval("{ x <- c(1L,1L,1L,1L); x[c(TRUE,TRUE,FALSE)] <- c('a','b','c'); x}", "\"a\", \"b\", \"1L\", \"c\"");
        assertEval("{ x <- c(TRUE,TRUE,TRUE,TRUE); x[c(TRUE,TRUE,FALSE)] <- list(10L,20L,30L); x }", "[[1]]\n10L\n\n[[2]]\n20L\n\n[[3]]\nTRUE\n\n[[4]]\n30L");

        assertEval("{ x <- c(); x[c('a','b')] <- c(1L,2L); x }", " a  b\n1L 2L");
        assertEval("{ x <- c(); x[c('a','b')] <- c(TRUE,FALSE); x }", "   a     b\nTRUE FALSE");
        assertEval("{ x <- c(); x[c('a','b')] <- c('a','b'); x }", "  a   b\n\"a\" \"b\"");
        assertEval("{ x <- list(); x[c('a','b')] <- c('a','b'); x }", "$a\n\"a\"\n\n$b\n\"b\"");
        assertEval("{ x <- list(); x[c('a','b')] <- list('a','b'); x }", "$a\n\"a\"\n\n$b\n\"b\"");
    }

    @Test
    public void testListDefinitions() throws RecognitionException {
        assertEval("{ list(1:4) }", "[[1]]\n1L, 2L, 3L, 4L");
        assertEval("{ list(1,list(2,list(3,4))) }", "[[1]]\n1.0\n\n[[2]]\n[[2]][[1]]\n2.0\n\n[[2]][[2]]\n[[2]][[2]][[1]]\n3.0\n\n[[2]][[2]][[2]]\n4.0");

        assertEval("{ list(1,b=list(2,3)) }", "[[1]]\n1.0\n\n$b\n$b[[1]]\n2.0\n\n$b[[2]]\n3.0");
        assertEval("{ list(1,b=list(c=2,3)) }", "[[1]]\n1.0\n\n$b\n$b$c\n2.0\n\n$b[[2]]\n3.0");
        assertEval("{ list(list(c=2)) }", "[[1]]\n[[1]]$c\n2.0");
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
        assertEval("{ l <- list(a=1,b=2,c=list(d=3,e=list(f=4))) ; l[[c(3,2)]] }", "$f\n4.0");
        assertEval("{ l <- list(a=1,b=2,c=list(d=3,e=list(f=4))) ; l[[c(3,1)]] }", "3.0");

        assertEval("{ l <- list(c=list(d=3,e=c(f=4)), b=2, a=3) ; l[[c(\"c\",\"e\")]] }", "  f\n4.0");
        assertEval("{ l <- list(c=list(d=3,e=c(f=4)), b=2, a=3) ; l[[c(\"c\",\"e\", \"f\")]] }", "4.0");
        assertEval("{ l <- list(c=list(d=3,e=c(f=4)), b=2, a=3) ; l[[c(\"c\")]] }", "$d\n3.0\n\n$e\n  f\n4.0");
    }

    @Test
    public void testListUpdate() throws RecognitionException {
        // scalar update
        assertEval("{ l<-list(1,2L,TRUE) ; l[[2]]<-100 ; l }", "[[1]]\n1.0\n\n[[2]]\n100.0\n\n[[3]]\nTRUE");
        assertEval("{ l<-list(1,2L,TRUE) ; l[[5]]<-100 ; l }", "[[1]]\n1.0\n\n[[2]]\n2L\n\n[[3]]\nTRUE\n\n[[4]]\nNULL\n\n[[5]]\n100.0");
        assertEval("{ l<-list(1,2L,TRUE) ; l[[3]]<-list(100) ; l }", "[[1]]\n1.0\n\n[[2]]\n2L\n\n[[3]]\n[[3]][[1]]\n100.0");
        assertEval("{ v<-1:3 ; v[2] <- list(100) ; v }", "[[1]]\n1L\n\n[[2]]\n100.0\n\n[[3]]\n3L");
        assertEval("{ v<-1:3 ; v[[2]] <- list(100) ; v }", "[[1]]\n1L\n\n[[2]]\n[[2]][[1]]\n100.0\n\n[[3]]\n3L");
        assertEval("{ l <- list() ; l[[1]] <-2 ; l}", "[[1]]\n2.0");
        assertEval("{ l<-list() ; x <- 1:3 ; l[[1]] <- x  ; l }", "[[1]]\n1L, 2L, 3L");
        assertEval("{ l <- list(1,2,3) ; l[2] <- list(100) ; l[2] }", "[[1]]\n100.0");
        assertEval("{ l <- list(1,2,3) ; l[[2]] <- list(100) ; l[2] }", "[[1]]\n[[1]][[1]]\n100.0");

        // element deletion
        assertEval("{ m<-list(1,2) ; m[TRUE] <- NULL ; m }", "list()");
        assertEval("{ m<-list(1,2) ; m[[TRUE]] <- NULL ; m }", "[[1]]\n2.0");
        assertEval("{ m<-list(1,2) ; m[[1]] <- NULL ; m }", "[[1]]\n2.0");
        assertEval("{ m<-list(1,2) ; m[[-1]] <- NULL ; m }", "[[1]]\n1.0");
        assertEval("{ m<-list(1,2) ; m[[-2]] <- NULL ; m }", "[[1]]\n2.0");
        assertEval("{ l <- matrix(list(1,2)) ; l[3] <- NULL ; l }", "[[1]]\n1.0\n\n[[2]]\n2.0");
        assertEval("{ l <- matrix(list(1,2)) ; l[[3]] <- NULL ; l }", "     [,1]\n[1,]  1.0\n[2,]  2.0");
        assertEval("{ l <- matrix(list(1,2)) ; l[[4]] <- NULL ; l }", "     [,1]\n[1,]  1.0\n[2,]  2.0");
        assertEval("{ l <- matrix(list(1,2)) ; l[4] <- NULL ; l }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\nNULL");
        assertEval("{ l <- list(a=1,b=2,c=3) ; l[1] <- NULL ; l }", "$b\n2.0\n\n$c\n3.0");
        assertEval("{ l <- list(a=1,b=2,c=3) ; l[3] <- NULL ; l }", "$a\n1.0\n\n$b\n2.0");

        assertEval("{ l <- list(a=1,b=2,c=3) ; l[5] <- NULL ; l}", "$a\n1.0\n\n$b\n2.0\n\n$c\n3.0\n\n[[4]]\nNULL");
        assertEval("{ l <- list(a=1,b=2,c=3) ; l[4] <- NULL ; l}", "$a\n1.0\n\n$b\n2.0\n\n$c\n3.0");
        assertEval("{ l <- list(a=1,b=2,c=3) ; l[[5]] <- NULL ; l}", "$a\n1.0\n\n$b\n2.0\n\n$c\n3.0");
        assertEval("{ l <- list(a=1,b=2,c=3) ; l[[4]] <- NULL ; l}", "$a\n1.0\n\n$b\n2.0\n\n$c\n3.0");

        assertEval("{ l <- list(1,2); l[0] <- NULL; l}", "[[1]]\n1.0\n\n[[2]]\n2.0");
        assertEvalError("{ l <- list(1,2); l[[0]] }", "attempt to select less than one element");

        // vector update
        assertEval("{ l <- list(1,2,3) ; l[c(2,3)] <- c(20,30) ; l }", "[[1]]\n1.0\n\n[[2]]\n20.0\n\n[[3]]\n30.0");
        assertEval("{ l <- list(1,2,3) ; l[c(2:3)] <- c(20,30) ; l }", "[[1]]\n1.0\n\n[[2]]\n20.0\n\n[[3]]\n30.0");
        assertEval("{ l <- list(1,2,3) ; l[-1] <- c(20,30) ; l }", "[[1]]\n1.0\n\n[[2]]\n20.0\n\n[[3]]\n30.0");
        assertEval("{ l <- list(1,2,3) ; l[-1L] <- c(20,30) ; l }", "[[1]]\n1.0\n\n[[2]]\n20.0\n\n[[3]]\n30.0");
        assertEval("{ l <- list(1,2,3) ; l[c(FALSE,TRUE,TRUE)] <- c(20,30) ; l }", "[[1]]\n1.0\n\n[[2]]\n20.0\n\n[[3]]\n30.0");
        assertEval("{ l <- list() ; l[c(TRUE,TRUE)] <-2 ; l }", "[[1]]\n2.0\n\n[[2]]\n2.0");
        assertEval("{ x <- 1:3 ; l <- list(1) ; l[[TRUE]] <- x ; l[[1]] } ", "1L, 2L, 3L");

        assertEval("{ x<-list(1,2,3,4,5); x[3:4]<-c(300L,400L); x }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n300L\n\n[[4]]\n400L\n\n[[5]]\n5.0");
        assertEval("{ x<-list(1,2,3,4,5); x[4:3]<-c(300L,400L); x }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n400L\n\n[[4]]\n300L\n\n[[5]]\n5.0");
        assertEval("{ x<-list(1,2L,TRUE,TRUE,FALSE); x[c(-2,-3,-3,-100,0)]<-256; x }", "[[1]]\n256.0\n\n[[2]]\n2L\n\n[[3]]\nTRUE\n\n[[4]]\n256.0\n\n[[5]]\n256.0");
        assertEval("{ x<-list(1,2L,list(3,list(4)),list(5)) ; x[c(4,2,3)]<-list(256L,257L,258L); x }", "[[1]]\n1.0\n\n[[2]]\n257L\n\n[[3]]\n258L\n\n[[4]]\n256L");
        assertEval("{ x<-list(FALSE,NULL,3L,4L,5.5); x[c(TRUE,FALSE)] <- 1000; x }", "[[1]]\n1000.0\n\n[[2]]\nNULL\n\n[[3]]\n1000.0\n\n[[4]]\n4L\n\n[[5]]\n1000.0");
        assertEval("{ x<-list(11,10,9) ; x[c(TRUE, FALSE, TRUE)] <- c(1000,2000); x }", "[[1]]\n1000.0\n\n[[2]]\n10.0\n\n[[3]]\n2000.0");
        assertEval("{ l <- list(1,2,3) ; x <- list(100) ; y <- x; l[1:1] <- x ; l[[1]] }", "100.0");
        assertEval("{ l <- list(1,2,3) ; x <- list(100) ; y <- x; l[[1:1]] <- x ; l[[1]] }", "[[1]]\n100.0");

        // vector element deletion
        assertEval("{ v<-list(1,2,3) ; v[c(2,3,NA,7,0)] <- NULL ; v }", "[[1]]\n1.0\n\n[[2]]\nNULL\n\n[[3]]\nNULL\n\n[[4]]\nNULL");
        assertEval("{ v<-list(1,2,3) ; v[c(2,3,4)] <- NULL ; v }", "[[1]]\n1.0");
        assertEval("{ v<-list(1,2,3) ; v[c(-1,-2,-6)] <- NULL ; v }", "[[1]]\n1.0\n\n[[2]]\n2.0");
        assertEval("{ v<-list(1,2,3) ; v[c(TRUE,FALSE,TRUE)] <- NULL ; v }", "[[1]]\n2.0");
        assertEval("{ v<-list(1,2,3) ; v[c()] <- NULL ; v }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n3.0");
        assertEval("{ v<-list(1,2,3) ; v[integer()] <- NULL ; v }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n3.0");
        assertEval("{ v<-list(1,2,3) ; v[double()] <- NULL ; v }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n3.0");
        assertEval("{ v<-list(1,2,3) ; v[logical()] <- NULL ; v }", "[[1]]\n1.0\n\n[[2]]\n2.0\n\n[[3]]\n3.0");
        assertEval("{ v<-list(1,2,3) ; v[c(TRUE,FALSE)] <- NULL ; v }", "[[1]]\n2.0");
        assertEval("{ v<-list(1,2,3) ; v[c(TRUE,FALSE,FALSE,FALSE,FALSE,TRUE)] <- NULL ; v }", "[[1]]\n2.0\n\n[[2]]\n3.0\n\n[[3]]\nNULL\n\n[[4]]\nNULL");

        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(-1,-3)] <- NULL ; l}", "$a\n1.0\n\n$c\n3.0");
        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(-1,-10)] <- NULL ; l}", "$a\n1.0");
        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(2,3)] <- NULL ; l}", "$a\n1.0\n\n$d\n4.0");
        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(2,3,5)] <- NULL ; l}", "$a\n1.0\n\n$d\n4.0");
        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(2,3,6)] <- NULL ; l}", "$a\n1.0\n\n$d\n4.0\n\n[[3]]\nNULL");
        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(TRUE,TRUE,FALSE,TRUE)] <- NULL ; l}", "$c\n3.0");
        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(TRUE,FALSE)] <- NULL ; l}", "$b\n2.0\n\n$d\n4.0");
        assertEval("{ l<-list(a=1,b=2,c=3,d=4); l[c(TRUE,FALSE,FALSE,TRUE,FALSE,NA,TRUE,TRUE)] <- NULL ; l}", "$b\n2.0\n\n$c\n3.0\n\n[[3]]\nNULL\n\n[[4]]\nNULL");

        assertEval("{ l <- list(a=1,b=2,c=3) ; l[[\"b\"]] <- NULL ; l }", "$a\n1.0\n\n$c\n3.0");

        // recursive indexing
        assertEval("{ l <- list(1,list(2,c(3))) ; l[[c(2,2)]] <- NULL ; l }", "[[1]]\n1.0\n\n[[2]]\n[[2]][[1]]\n2.0");
        assertEval("{ l <- list(1,list(2,c(3))) ; l[[c(2,2)]] <- 4 ; l }", "[[1]]\n1.0\n\n[[2]]\n[[2]][[1]]\n2.0\n\n[[2]][[2]]\n4.0");
        assertEval("{ l <- list(1,list(2,list(3))) ; l[[1]] <- NULL ; l }", "[[1]]\n[[1]][[1]]\n2.0\n\n[[1]][[2]]\n[[1]][[2]][[1]]\n3.0");
        assertEval("{ l <- list(1,list(2,list(3))) ; l[[1]] <- 5 ; l }", "[[1]]\n5.0\n\n[[2]]\n[[2]][[1]]\n2.0\n\n[[2]][[2]]\n[[2]][[2]][[1]]\n3.0");

        assertEval("{ l<-list(a=1,b=2,list(c=3,d=4,list(e=5:6,f=100))) ; l[[c(3,3,1)]] <- NULL ; l }", "$a\n1.0\n\n$b\n2.0\n\n[[3]]\n[[3]]$c\n3.0\n\n[[3]]$d\n4.0\n\n[[3]][[3]]\n[[3]][[3]]$f\n100.0");
        assertEval("{ l<-list(a=1,b=2,c=list(d=1,e=2,f=c(x=1,y=2,z=3))) ; l[[c(\"c\",\"f\",\"zz\")]] <- 100 ; l }", "$a\n1.0\n\n$b\n2.0\n\n$c\n$c$d\n1.0\n\n$c$e\n2.0\n\n$c$f\n  x   y   z    zz\n1.0 2.0 3.0 100.0");
        assertEval("{ l<-list(a=1,b=2,c=list(d=1,e=2,f=c(x=1,y=2,z=3))) ; l[[c(\"c\",\"f\",\"z\")]] <- 100 ; l }", "$a\n1.0\n\n$b\n2.0\n\n$c\n$c$d\n1.0\n\n$c$e\n2.0\n\n$c$f\n  x   y     z\n1.0 2.0 100.0");
        assertEval("{ l<-list(a=1,b=2,c=list(d=1,e=2,f=c(x=1,y=2,z=3))) ; l[[c(\"c\",\"f\")]] <- NULL ; l }", "$a\n1.0\n\n$b\n2.0\n\n$c\n$c$d\n1.0\n\n$c$e\n2.0");
        assertEval("{ l <- list(a=1,b=2,c=3) ; l[c(\"a\",\"a\",\"a\",\"c\")] <- NULL ; l }", "$b\n2.0");
        assertEval("{ l<-list(a=1L,b=2L,c=list(d=1L,e=2L,f=c(x=1L,y=2L,z=3L))) ; l[[c(\"c\",\"f\",\"zz\")]] <- 100L ; l }", "$a\n1L\n\n$b\n2L\n\n$c\n$c$d\n1L\n\n$c$e\n2L\n\n$c$f\n x  y  z   zz\n1L 2L 3L 100L");
        assertEval("{ l<-list(a=TRUE,b=FALSE,c=list(d=TRUE,e=FALSE,f=c(x=TRUE,y=FALSE,z=TRUE))) ; l[[c(\"c\",\"f\",\"zz\")]] <- TRUE ; l }", "$a\nTRUE\n\n$b\nFALSE\n\n$c\n$c$d\nTRUE\n\n$c$e\nFALSE\n\n$c$f\n   x     y    z   zz\nTRUE FALSE TRUE TRUE");
        assertEval("{ l<-list(a=\"a\",b=\"b\",c=list(d=\"cd\",e=\"ce\",f=c(x=\"cfx\",y=\"cfy\",z=\"cfz\"))) ; l[[c(\"c\",\"f\",\"zz\")]] <- \"cfzz\" ; l }", "$a\n\"a\"\n\n$b\n\"b\"\n\n$c\n$c$d\n\"cd\"\n\n$c$e\n\"ce\"\n\n$c$f\n    x     y     z     zz\n\"cfx\" \"cfy\" \"cfz\" \"cfzz\"");

        assertEval("{ l<-list(a=1,b=2,c=list(d=1,e=2,f=c(x=1,y=2,z=3))) ; l[[c(\"c\",\"f\",\"zz\")]] <- list(100) ; l }", "$a\n1.0\n\n$b\n2.0\n\n$c\n$c$d\n1.0\n\n$c$e\n2.0\n\n$c$f\n$c$f$x\n1.0\n\n$c$f$y\n2.0\n\n$c$f$z\n3.0\n\n$c$f$zz\n$c$f$zz[[1]]\n100.0");
        assertEval("{ l<-list(a=1L,b=2L,c=list(d=1L,e=2L,f=c(x=1L,y=2L,z=3L))) ; l[[c(\"c\",\"f\")]] <- 100L ; l }", "$a\n1L\n\n$b\n2L\n\n$c\n$c$d\n1L\n\n$c$e\n2L\n\n$c$f\n100L");
        assertEval("{ l<-list(a=1L,b=2L,c=list(d=1L,e=2L,f=c(x=1L,y=2L,z=3L))) ; l[[c(\"c\",\"f\")]] <- list(haha=\"gaga\") ; l }", "$a\n1L\n\n$b\n2L\n\n$c\n$c$d\n1L\n\n$c$e\n2L\n\n$c$f\n$c$f$haha\n\"gaga\"");


        // copying
        assertEval("{ x<-c(1,2,3) ; y<-x ; x[2]<-100 ; y }", "1.0, 2.0, 3.0");
        assertEval("{ l<-list() ; x <- 1:3 ; l[[1]] <- x; x[2] <- 100L; l[[1]] }", "1L, 2L, 3L");
        assertEval("{ l <- list(1, list(2)) ;  m <- l ; l[[c(2,1)]] <- 3 ; m[[2]][[1]] }", "2.0");
        assertEval("{ l <- list(1, list(2,3,4)) ;  m <- l ; l[[c(2,1)]] <- 3 ; m[[2]][[1]] }", "2.0");
        assertEval("{ x <- c(1L,2L,3L) ; l <- list(1) ; l[[1]] <- x ; x[2] <- 100L ; l[[1]] }", "1L, 2L, 3L");
        assertEval("{ l <- list(100) ; f <- function() { l[[1]] <- 2 } ; f() ; l }", "[[1]]\n100.0");
        assertEval("{ l <- list(100,200,300,400,500) ; f <- function() { l[[3]] <- 2 } ; f() ; l }", "[[1]]\n100.0\n\n" +
                "[[2]]\n200.0\n\n" +
                "[[3]]\n300.0\n\n" +
                "[[4]]\n400.0\n\n" +
                "[[5]]\n500.0");
        assertEval("{ x <-2L ; y <- x; x[1] <- 211L ; y }", "2L");
        assertEval("{ f <- function() { l[1:2] <- x ; x[1] <- 211L  ; l[1] } ; l <- 1:3 ; x <- 10L ; f() }", "10L");
    }

    @Test
    public void testStringUpdate() throws RecognitionException {
        assertEval("{ a <- 'hello'; a[[5]] <- 'done'; a[[3]] <- 'muhuhu'; a; }", "\"hello\", NA, \"muhuhu\", NA, \"done\"");
        assertEval("{ a <- 'hello'; a[[5]] <- 'done'; b <- a; b[[3]] <- 'muhuhu'; b; }", "\"hello\", NA, \"muhuhu\", NA, \"done\"");
    }

    @Test
    public void testGenericUpdate() throws RecognitionException {
        assertEval("{ a <- TRUE; a[[2]] <- FALSE; a; }", "TRUE, FALSE");
    }

    @Test
    public void testSuperUpdate() throws RecognitionException {
        assertEval("{ x <- 1:3 ; f <- function() { x[2] <<- 100 } ; f() ; x }", "1.0, 100.0, 3.0");
        assertEval("{ x <- 1:3 ; f <- function() { x[2] <- 10 ; x[2] <<- 100 ; x[2] <- 1000 } ; f() ; x }", "1.0, 100.0, 3.0");
    }

    @Test
    public void testMatrixIndex() throws RecognitionException {
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[1,2] }", "3L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[1,] }", "1L, 3L, 5L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[1,,drop=FALSE] }", "     [,1] [,2] [,3]\n[1,]   1L   3L   5L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,1] }", "1L, 2L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,] }", "     [,1] [,2] [,3]\n[1,]   1L   3L   5L\n[2,]   2L   4L   6L");

        assertEval("{ m <- matrix(1:6, nrow=2) ; m[1:2,2:3] }", "     [,1] [,2]\n[1,]   3L   5L\n[2,]   4L   6L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[1:2,-1] }", "     [,1] [,2]\n[1,]   3L   5L\n[2,]   4L   6L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,-1] }", "     [,1] [,2]\n[1,]   3L   5L\n[2,]   4L   6L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,c(-1,0,0,-1)] }", "     [,1] [,2]\n[1,]   3L   5L\n[2,]   4L   6L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,c(1,NA,1,NA)] }", "     [,1] [,2] [,3] [,4]\n[1,]   1L   NA   1L   NA\n[2,]   2L   NA   2L   NA");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,1[2],drop=FALSE] }", "     [,1]\n[1,]   NA\n[2,]   NA");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,c(NA,1,0)] }", "     [,1] [,2]\n[1,]   NA   1L\n[2,]   NA   2L");

        assertEval("{ m <- matrix(1:16, nrow=8) ; m[c(TRUE,FALSE,FALSE),c(FALSE,NA), drop=FALSE]}", "     [,1]\n[1,]   NA\n[2,]   NA\n[3,]   NA");
        assertEval("{ m <- matrix(1:16, nrow=8) ; m[c(TRUE,FALSE),c(FALSE,TRUE), drop=TRUE]}", "9L, 11L, 13L, 15L");
        assertEval("{ m <- matrix(1:16, nrow=8) ; m[c(TRUE,FALSE,FALSE),c(FALSE,TRUE), drop=TRUE]}", "9L, 12L, 15L");

        assertEval("{ m <- matrix(1:6, nrow=3) ; f <- function(i,j) { m[i,j] } ; f(1,c(1,2)) ; f(1,c(-1,0,-1,-10)) }", "4L");
        assertEval("{ m <- matrix(1:6, nrow=3) ; f <- function(i,j) { m[i,j] } ; f(1,c(1,2)) ; f(c(TRUE),c(FALSE,TRUE)) }", "4L, 5L, 6L");

        assertEval("{ m <- matrix(1:6, nrow=2) ; x<-2 ; m[[1,x]] }", "3L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[[1,2]] }", "3L");

        assertEval("{ m <- matrix(1:6, nrow=2) ; f <- function(i,j) { m[i,j] } ;  f(1,1); f(1,1:3) }", "1L, 3L, 5L");
    }

    @Test
    public void testIn() throws RecognitionException {
        assertEval("{ 1:3 %in% 1:10 }", "TRUE, TRUE, TRUE");
        assertEval("{ 1 %in% 1:10 }", "TRUE");
        assertEval("{ c(\"1L\",\"hello\") %in% 1:10 }", "TRUE, FALSE");
    }

    @Test
    public void testEmptyUpdate() throws RecognitionException {
        assertEval("{ a <- list(); a$a = 6; a; }", "$a\n6.0");
        assertEval("{ a <- list(); a[['b']] = 6; a; }", "$b\n6.0");
    }

    @Test
    public void testFieldAccess() throws RecognitionException {
        assertEval("{ a <- list(a = 1, b = 2); a$a; }", "1.0");
        assertEval("{ a <- list(a = 1, b = 2); a$b; }", "2.0");
        assertEval("{ a <- list(a = 1, b = 2); a$c; }", "$null\nNULL");
        assertEval("{ a <- list(a = 1, b = 2); a$a <- 67; a; }", "$a\n67.0\n\n$b\n2.0");
        assertEval("{ a <- list(a = 1, b = 2); a$b <- 67; a; }", "$a\n1.0\n\n$b\n67.0");
        assertEval("{ a <- list(a = 1, b = 2); a$c <- 67; a; }", "$a\n1.0\n\n$b\n2.0\n\n$c\n67.0");
    }
}

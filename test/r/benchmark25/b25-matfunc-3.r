# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# II. Matrix functions
# Determinant of a 2500x2500 random matrix

b25matfunc <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 11L

  for (i in 1:runs) {
    a <- rnorm(2500*2500)
    dim(a) <- c(2500, 2500)
    b <- det(a)
  }
}

if (!exists("i_am_wrapper")) {
  b25matfunc(commandArgs(trailingOnly=TRUE))
}

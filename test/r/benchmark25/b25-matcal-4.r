# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# I. Matrix calculation
# 2800x2800 cross-product matrix (b = a' * a)

b25matcal <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 9L

  for (i in 1:runs) {
    a <- rnorm(2800*2800)
    dim(a) <- c(2800, 2800)
    b <- crossprod(a)    # equivalent to: b <- t(a) %*% a
  }
}

if (!exists("i_am_wrapper")) {
  b25matcal(commandArgs(trailingOnly=TRUE))
}

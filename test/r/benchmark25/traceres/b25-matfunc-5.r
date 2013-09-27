# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# II. Matrix functions
# Inverse of a 1600x1600 random matrix

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matfunc <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 2L

  res <- 0
  for (i in 1:runs) {
    a <- rnorm(1600*1600)
    dim(a) <- c(1600, 1600)
    b <- qr.solve(a)
    res <- res + sum(b)
  }

  # CTK: to ensure materialization, and to get a result to check
  round( res, digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  b25matfunc(commandArgs(trailingOnly=TRUE))
}

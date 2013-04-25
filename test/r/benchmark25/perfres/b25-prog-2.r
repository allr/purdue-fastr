# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# Creation of a 3000x3000 Hilbert matrix (matrix calc)

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25prog <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 48L

  a <- 3000

  res <- 0
  for (i in 1:runs) {
    b <- rep(1:a, a)
    dim(b) <- c(a, a)
    b <- 1 / (t(b) + 0:(a-1))
    # CTK: to ensure materialization, and to get a result to check
    res <- res + sum(b)
  }

  round( res / runs, digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  b25prog(commandArgs(trailingOnly=TRUE))
}

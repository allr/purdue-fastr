# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# I. Matrix calculation
# Sorting of 7,000,000 random values

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matcal <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 12L

  res <- 0
  for (i in 1:runs) {
    a <- rnorm(7000)
    b <- sort(a, method="quick")
    # CTK: to ensure materialization, and to get a result to check
    res <- res + sum(b * (1:70))
  }

  round( res / runs, digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  b25matcal(commandArgs(trailingOnly=TRUE))
}

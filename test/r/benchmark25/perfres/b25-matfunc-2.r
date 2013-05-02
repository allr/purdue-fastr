# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# II. Matrix functions
# Eigenvalues of a 600x600 random matrix

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matfunc <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 19L

  res <- 0
  for (i in 1:runs) {
    a <- array(rnorm(600*600), dim = c(600, 600))
    b <- eigen(a, symmetric=FALSE, only.values=TRUE)$values
        # the 2.5 version of the benchmark uses $Value instead of $values but that is not working with R

    # CTK: to ensure materialization, and to get a result to check
    res <- res + sum(b)
  }

  round( res / runs, digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  b25matfunc(commandArgs(trailingOnly=TRUE))
}

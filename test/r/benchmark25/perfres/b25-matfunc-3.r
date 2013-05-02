# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# II. Matrix functions
# Determinant of a 2500x2500 random matrix

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matfunc <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 11L

  cnt <- 0
  for (i in 1:runs) {
    a <- rnorm(2500*2500)
    dim(a) <- c(2500, 2500)
    b <- det(a)

    # CTK: to get a result to check
    # note, with the input data, the determinant is always either -Inf or +Inf,
    # so we are just counting how many times it is -Inf

    if (b < 1/0) {
      cnt <- cnt + 1
    }
  }

  cnt
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  b25matfunc(commandArgs(trailingOnly=TRUE))
}

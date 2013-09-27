# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# Grand common divisors of 400,000 pairs (recursion)

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

gcd2 <- function(x, y) {
  if (sum(y > 1.0E-4) == 0) {
    x
  } else {
    y[y == 0] <- x[y == 0]
    Recall(y, x %% y)    # recursive call to gcd2
  }
}

b25prog <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 32L

  res <- 0
  for (i in 1:runs) {
    a <- ceiling(runif(40)*1000)
    b <- ceiling(runif(40)*1000)
    c <- gcd2(a, b)    # gcd2 is a recursive function

    # CTK: to ensure materialization, and to get a result to check
    res <- res + sum(c)
  }

  round( res / runs, digits = 5)
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  b25prog(commandArgs(trailingOnly=TRUE))
}

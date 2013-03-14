# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# Grand common divisors of 400,000 pairs (recursion)

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

  for (i in 1:runs) {
    a <- ceiling(runif(400000)*1000)
    b <- ceiling(runif(400000)*1000)
    c <- gcd2(a, b)    # gcd2 is a recursive function
  }
}

if (!exists("i_am_wrapper")) {
  b25prog(commandArgs(trailingOnly=TRUE))
}

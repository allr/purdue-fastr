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

b25prog <- function() {
  a <- ceiling(runif(400)*1000)
  b <- ceiling(runif(400)*1000)
  c <- gcd2(a, b)    # gcd2 is a recursive function

  # CTK: to ensure materialization, and to get a result to check
  sum(c)
}

b25prog()

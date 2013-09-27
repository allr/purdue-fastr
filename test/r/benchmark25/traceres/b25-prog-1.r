# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# 3,500,000 Fibonacci numbers calculation (vector calc)

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25prog <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 21L

  phi <- 1.6180339887498949

  res <- 0
  for (i in 1:runs) {
    a <- floor(runif(350)*1000)
    b <- (phi^a - (-phi)^(-a))/sqrt(5)

     # CTK: to ensure materialization, and to get a result to check
    res <- res + log(sum(b))
  }

  round( res / runs, digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  b25prog(commandArgs(trailingOnly=TRUE))
}

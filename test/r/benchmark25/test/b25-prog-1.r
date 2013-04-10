# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# 3,500,000 Fibonacci numbers calculation (vector calc)

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25prog <- function() {

  phi <- 1.6180339887498949
  a <- floor(runif(3500)*1000)
  b <- (phi^a - (-phi)^(-a))/sqrt(5)

  # CTK: to ensure materialization, and to get a result to check
  round( log(sum(b)), digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

b25prog()

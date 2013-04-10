# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# II. Matrix functions
# FFT over 2,400,000 random values

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matfunc <- function() {
  a <- rnorm(2400)
  b <- fft(a)

  # CTK: to ensure materialization, and to get a result to check
  round( sum(b), digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

b25matfunc()    

# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# II. Matrix functions
# FFT over 2,400,000 random values

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matfunc <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 26L

  for (i in 1:runs) {
    a <- rnorm(2400000)
    b <- fft(a)
  }
}

if (!exists("i_am_wrapper")) {
  b25matfunc(commandArgs(trailingOnly=TRUE))
}

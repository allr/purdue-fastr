# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# I. Matrix calculation
# 2500x2500 normal distributed random matrix ^1000

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matcal <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 13L

  for (i in 1:runs) {
    a <- abs(matrix(rnorm(2500*2500)/2, ncol=2500, nrow=2500))
    b <- a^1000
  }
}

if (!exists("i_am_wrapper")) {
  b25matcal(commandArgs(trailingOnly=TRUE))
}

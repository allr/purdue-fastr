# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# I. Matrix calculation
# Creation, transp., deformation of a 2500x2500 matrix

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matcal <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 22L

  for (i in 1:runs) {
    a <- matrix(rnorm(2500*2500)/10, ncol=2500, nrow=2500)
    b <- t(a)
    dim(b) <- c(1250, 5000)
    a <- t(b)
  }
}

if (!exists("i_am_wrapper")) {
  b25matcal(commandArgs(trailingOnly=TRUE))
}

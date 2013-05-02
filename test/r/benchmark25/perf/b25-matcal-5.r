# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# I. Matrix calculation
# Linear regr. over a 3000x3000 matrix (c = a \\ b')

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matcal <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 1L

  for (i in 1:runs) {
    a <- rnorm(3000*3000)
    dim(a) <- c(3000,3000)
    b <- 1:3000
    qra <- qr(a, tol = 1e-7)
    c <- qr.coef(qra, b)
  }
}

if (!exists("i_am_wrapper")) {
  b25matcal(commandArgs(trailingOnly=TRUE))
}

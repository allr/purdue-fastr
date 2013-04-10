# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# Creation of a 500x500 Toeplitz matrix (loops)

b25prog <- function() {
  b <- rep(0, 100*100)
  dim(b) <- c(100, 100)

      # Rem: there are faster ways to do this
      # but here we want to time loops (220*220 'for' loops)!

  for (j in 1:100) {
    for (k in 1:100) {
      b[k,j] <- abs(j - k) + 1
    }
  }

  # CTK: to ensure materialization, and to get a result to check
  sum(b)
}

b25prog()

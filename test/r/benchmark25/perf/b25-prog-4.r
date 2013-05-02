# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# Creation of a 500x500 Toeplitz matrix (loops)

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25prog <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 105L

  for (i in 1:runs) {
    b <- rep(0, 500*500)
    dim(b) <- c(500, 500)

        # Rem: there are faster ways to do this
        # but here we want to time loops (220*220 'for' loops)!

    for (j in 1:500) {
      for (k in 1:500) {
        b[k,j] <- abs(j - k) + 1
      }
    }
  }
}

if (!exists("i_am_wrapper")) {
  b25prog(commandArgs(trailingOnly=TRUE))
}

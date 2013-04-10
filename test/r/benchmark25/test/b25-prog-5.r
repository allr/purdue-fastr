# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# Escoufier's method on a 45x45 matrix (mixed)

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

Trace <- function(y) {
  sum(c(y)[1 + 0:(min(dim(y)) - 1) * (dim(y)[1] + 1)], na.rm=FALSE)
}

b25prog <- function() {
  x <- abs(rnorm(15*15))
  dim(x) <- c(15, 15)

      # Calculation of Escoufier's equivalent vectors
  p <- ncol(x)
  vt <- 1:p                                  # Variables to test
  vr <- NULL                                 # Result: ordered variables
  RV <- 1:p                                  # Result: correlations
  vrt <- NULL
  for (j in 1:p) {                           # loop on the variable number
    Rvmax <- 0
    for (k in 1:(p-j+1)) {                   # loop on the variables
      x2 <- cbind(x, x[,vr], x[,vt[k]])
      R <- cor(x2)                           # Correlations table
      Ryy <- R[1:p, 1:p]
      Rxx <- R[(p+1):(p+j), (p+1):(p+j)]
      Rxy <- R[(p+1):(p+j), 1:p]
      Ryx <- t(Rxy)
      rvt <- Trace(Ryx %*% Rxy) / sqrt(Trace(Ryy %*% Ryy) * Trace(Rxx %*% Rxx)) # RV calculation
      if (rvt > Rvmax) {
        Rvmax <- rvt                         # test of RV
        vrt <- vt[k]                         # temporary held variable
      }
    }
    vr[j] <- vrt                             # Result: variable
    RV[j] <- Rvmax                           # Result: correlation
    vt <- vt[vt!=vr[j]]                      # reidentify variables to test
  }

  # CTK: to ensure materialization, and to get a result to check
  round( sum(vr) * sum(RV), digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

b25prog()

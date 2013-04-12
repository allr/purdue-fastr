# ------------------------------------------------------------------
# Contributed by Michel Lang, TU Dortmund
# ------------------------------------------------------------------
# Code from https://github.com/fonnesbeck/useRshootout/blob/master/R/simplegibbs.R
# Obtain a sequence of random samples from multivariate probability distribution
# http://en.wikipedia.org/wiki/Gibbs_sampling

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

Rgibbs <- function(N,thin) {
    mat <- matrix(0,ncol=2,nrow=N)
    x <- 0
    y <- 0
    for (i in 1:N) {
        for (j in 1:thin) {
            x <- rgamma(1,3,y*y+4)
            y <- rnorm(1,1/(x+1),1/sqrt(2*(x+1)))
        }
        mat[i,] <- c(x,y)
    }
    round( sum(mat), digits=5 )
}

# change these numbers to get a suitable runtime
#Rgibbs(1000, 100)

gibbs <- function(args) {
  thin = if (length(args)) as.integer(args[[1]]) else 460L
  Rgibbs(100, thin)
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

if (!exists("i_am_wrapper")) {
  gibbs(commandArgs(trailingOnly=TRUE))
}



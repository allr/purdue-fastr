# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# I. Matrix calculation
# Creation, transp., deformation of a 2500x2500 matrix

# CTK: to ensure repeatability, this is the seed used by libRmath
.Random.seed = c(401L,1234L,5678L)

b25matcal <- function() {
  a <- matrix(rnorm(25*25)/10, ncol=25, nrow=25)
  b <- t(a)
  dim(b) <- c(125, 5)
  a <- t(b)
  
  # CTK: to ensure materialization, and to get a result to check
  round( sum(a), digits=5 )
}

# CTK: to make GNU-R print all 5 decimal digits
options(digits=15)

b25matcal()

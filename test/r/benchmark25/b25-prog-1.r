# extracted from R Benchmark 2.5 (06/2008) [Simon Urbanek]
# http://r.research.att.com/benchmarks/R-benchmark-25.R

# III. Programming
# 3,500,000 Fibonacci numbers calculation (vector calc)

b25prog <- function(args) {
  runs = if (length(args)) as.integer(args[[1]]) else 21L

  phi <- 1.6180339887498949

  for (i in 1:runs) {
    a <- floor(runif(3500000)*1000)
    b <- (phi^a - (-phi)^(-a))/sqrt(5)
  }
}

if (!exists("i_am_wrapper")) {
  b25prog(commandArgs(trailingOnly=TRUE))
}

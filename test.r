#f <- function(a,b) {
#  a + b
#}
#f2 <- function() {
#  for (i in 1:10) {
#    cat(f(i, i+1),"\n")
#  }
#}
#
#f2()

eval_A <- function(i, j) 1 / ((i + j) * (i + j + 1) / 2 + i + 1)

spectralnorm_naive <- function(args) {
    n = if (length(args)) as.integer(args[[1]]) else 100L
    options(digits=10)

    eval_A_times_u <- function(u) {
        ret <- rep(0, n)
        for (i in 1:n)
	    for (j in 0:n1)
                ret[[i]] <- ret[[i]] + u[[j + 1]] * eval_A(i - 1, j)
        return(ret)
    }
    eval_At_times_u <- function(u) {
        ret <- rep(0, n)
        for (i in 1:n)
	    for (j in 0:n1)
                ret[[i]] <- ret[[i]] + u[[j + 1]] * eval_A(j, i - 1)
        return(ret)
    }
    eval_AtA_times_u <- function(u) eval_At_times_u(eval_A_times_u(u))

    n1 <- n - 1
    u <- rep(1, n)
    v <- rep(0, n)
    for (itr in seq(10)) {
        v <- eval_AtA_times_u(u)
        u <- eval_AtA_times_u(v)
    }

    cat(sqrt(sum(u * v) / sum(v * v)), "\n")
}

spectralnorm_naive(commandArgs(trailingOnly=TRUE))

# ------------------------------------------------------------------
# The Computer Language Shootout
# http://shootout.alioth.debian.org/
#
# Contributed by Leo Osvald
# ------------------------------------------------------------------

eval_A <- function(i, j) {
   1 / ((i + j) * (i + j + 1) / 2 + i + 1)
}

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


spectralnorm_naive <- function(args) {
    n <<- if (length(args)) as.integer(args[[1]]) else 200L
    options(digits=10)


    n1 <<- n - 1
    u <- rep(1, n)
    v <- rep(0, n)
    for (itr in seq(10)) {
        v <- eval_AtA_times_u(u)
        u <- eval_AtA_times_u(v)
    }

    cat(sqrt(sum(u * v) / sum(v * v)), "\n")
#   sqrt(sum(u * v) / sum(v * v)
}

spectralnorm_naive(200L)
spectralnorm_naive(200L)
spectralnorm_naive(200L)
spectralnorm_naive(200L)
spectralnorm_naive(200L)
t = _timerStart()
spectralnorm_naive(200L)
_timerEnd(t, "tmr")


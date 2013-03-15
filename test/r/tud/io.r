# ------------------------------------------------------------------
# Contributed by Michel Lang, TU Dortmund
# ------------------------------------------------------------------
### Test R's IO
# General loading and saving of files

initialize <- function(nobs = 1000) {
  # first time only: create some files to test on
  set.seed(1)
  dir.create(".tmp.example_io_files")

  # change these numbers to get a suitable runtime
  N <- 10 # number of files
  n <- nobs # number of observations
  p <- 300 # number of covariates

  for (i in 1:N) {
    X <- matrix(rnorm(n * p), n, p)
    colnames(X) <- sprintf("var_%03i", 1:p)
    X <- as.data.frame(X)
    save(X, file = file.path(".tmp.example_io_files", sprintf("data_%03i.RData", i)))
  }
}

####################################################################################
### benchmark code below
####################################################################################

iobench <- function() {
  # Iterate over files, load data into a list, scale and save back to file system
  fns <- list.files(".tmp.example_io_files", pattern = "^data_[0-9]+\\.RData$", full.names=TRUE)
  Xs <- vector("list", length(fns))

  for (i in seq_along(fns)) {
    ee <- new.env()
    load(fns[i], envir = ee)
    Xs[[i]] <- ee$X
  }

  Xs <- lapply(Xs, scale)

  for (i in seq_along(fns)) {
    X <- Xs[[i]]
    save(X, file = sub("data", "data_scaled", fns[i]))
  }
}

io <- function(args) {
  nobs = if (length(args)) as.integer(args[[1]]) else 5200L
  if (file.exists(".tmp.example_io_files")) {
    unlink(".tmp.example_io_files", recursive=TRUE)
  }
  initialize(nobs)
  iobench()
}

if (!exists("i_am_wrapper")) {
  io(commandArgs(trailingOnly=TRUE))
}

f <- function() {
  a + b
}

for (i in 1:10) {
  cat(f(i,i+1),"\n")
}
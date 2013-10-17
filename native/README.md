# Requirements

You need a valid R (2.1x.x series) installation or a prebuild source tree. This installation requires le R math standalone library.

- From sources:
    - Download R sources
		- Unpack them
		- `./configure`
		- `make ; make -C src/nmath/standalone/`
- From binaries:
    - ???

# Making native library working

If you are lucky just type: `make -C native`
If you are less luck type: `make -C native R_PATH=<the_full_path_to_your_source_tree_or_installation>`
Finally if it still do not work you'll have to hack a bit the `native/Makefile`.

# Running with native enabled

You need to launch R with the java.library.path property set to (at least) `$FASTR_ROOT/native`.
Not that your `LD_LIBRARY_PATH` should also contains `libRmath.xx` and `libR.xx`.
Note that on MacOS X `LD_LIBRARY_PATH` should be replaced by `DYLD_LIBRARY_PATH`

## With maven
	MAVEN\_OPTS="-Djava.library.path=`pwd`/native" mvn test

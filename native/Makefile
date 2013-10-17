OS=$(shell uname -s)
include Makefile.$(OS)

TARGETS=libgnurglue.so libsystemlibsglue.so

CFLAGS = -O3 -msse4 -fno-strict-aliasing -fPIC -fno-omit-frame-pointer -W -Wall -Wno-unused -Wno-unused-parameter -Wno-parentheses -I${JAVA_HOME}/include/ -I${MKLROOT}/include -I${JAVA_HOME}/include/${OS}/ -I${R_PATH}/include -I.
LDFLAGS+= -shared -L${R_PATH}/lib -L${R_PATH}/src/nmath/standalone/
LDLIBS=-lRmath -lR -lc
LIBS=$(patsubst %.so,%.$(LIB_EXT),$(TARGETS))

all: $(LIBS)

libgnurglue.so libgnurglue.dylib: r_ext_GNUR.o
	$(CC) -o $@ $(LDFLAGS) $^ $(LDLIBS)

libsystemlibsglue.so libsystemlibsglue.dylib: r_ext_SystemLibs.o
	$(CC) -o $@ $(LDFLAGS) $^ $(LDLIBS)

libmklglue.so: r_ext_MKL.o
	$(CC) -o $@ $(LDFLAGS) -L${MKLROOT}/lib/intel64 $^ $(LDLIBS) -lmkl_rt -liomp5 -ldl -lpthread -lm

clean:
	rm -f $(LIBS) *.o

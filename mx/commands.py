
import os, sys, shutil, hashlib;
from os.path import exists, join;
import mx;

# Graal VM module
gmod = None;
gvmOut = None;

# def vm(args, vm=None, nonZeroIsFatal=True, out=None, err=None, cwd=None, timeout=None, vmbuild=None):

def mx_init():
  commands = {
      'frtest': [frtest, ''],
      'r': [rconsoleServer, ''],
      'rg': [rconsoleGraal, ''],
      'rgd': [rconsoleDebugGraal, ''],
      'rfannkuch': [rfannkuchServer, '[size]'],
      'rgfannkuch': [rfannkuchGraal, '[size]'],
      'rbinarytrees': [rbinarytreesServer, '[size]'],
      'rgbinarytrees': [rbinarytreesGraal, '[size]'],
      'rspectralnorm': [rspectralnormServer, '[size]'],
      'rgspectralnorm': [rspectralnormGraal, '[size]'],
      'rnbody': [rnbodyServer, '[size]'],
      'rgnbody': [rnbodyGraal, '[size]'],
      'rfasta': [rfastaServer, '[size]'],
      'rgfasta': [rfastaGraal, '[size]'],
      'rfastaredux': [rfastareduxServer, '[size]'],
      'rgfastaredux': [rfastareduxGraal, '[size]'],
      'rpidigits': [rpidigitsServer, '[size]'],
      'rgpidigits': [rpidigitsGraal, '[size]'],
      'rregexdna': [rregexdnaServer, '[size]'],
      'rgregexdna': [rregexdnaGraal, '[size]'],
      'rmandelbrot': [rmandelbrotServer, '[size]'],
      'rgmandelbrot': [rmandelbrotGraal, '[size]'],
      'rreversecomplement': [rreversecomplementServer, '[size]'],
      'rgreversecomplement': [rreversecomplementGraal, '[size]'],
      'rknucleotide': [rknucleotideServer, '[size]'],
      'rgknucleotide': [rknucleotideGraal, '[size]'],
      'runittest': [runittestServer, ''],
      'rgunittest': [runittestGraal, ''],
      'rbenchmark': [rallbenchmarksServer, ''],
      'rgbenchmark': [rallbenchmarksGraal, '']
  }
  mx.commands.update(commands);

  # load the graal VM commands (the module that invoked us)
  gcommands = join(os.getcwd(), "mx","commands");
  mod = sys.modules.get(gcommands);

  if not hasattr(mod, 'mx_init'):
    mx.abort(gcommands + ' must define an mx_init(env) function - executing from Graal directory?');

  if not hasattr(mod, 'vm'):
    mx.abort(gcommands + ' does not have a vm command - executing from Graal directory?');

  global gmod;
  gmod = mod;

# ------------------  

def frtest(args):
  """Test FastR MX target"""
  mx.log("FastR classpath:");
  mx.log("mx.classpath(fastr) is "+mx.classpath("fastr"));
  mx.log("Server VM:");
  gmod.vm( ['-version'], vm = 'server' ); 
  mx.log("Graal VM:");
  gmod.vm( ['-XX:-BootstrapGraal', '-version'], vm = 'graal' ); 
  
def rconsoleServer(args):
  """Run R Console with the HotSpot server VM"""
  rconsole([], 'server', args)
  
def rconsoleGraal(args):
  """Run R Console with the Graal VM"""
  rconsole(['-XX:-BootstrapGraal'], 'graal', args)

def rconsoleDebugGraal(args):
  """Run R Console with the Graal VM, debugging options"""
  rconsole(['-XX:-BootstrapGraal', '-G:+DumpOnError', '-G:Dump=Truffle', '-G:+PrintBinaryGraphs', '-G:+PrintCFG', '-esa'], 'graal', args)

def rfannkuchServer(args):
  """Run Fannkuch with the HotSpot server VM"""
  rfannkuch(args, [], 'server')

def rfannkuchGraal(args):
  """Run Fannkuch with the Graal VM"""
  rfannkuch(args, ['-XX:-BootstrapGraal'], 'graal')

def rbinarytreesServer(args):
  """Run Binary Trees with the HotSpot server VM"""
  rbinarytrees(args, [], 'server')

def rbinarytreesGraal(args):
  """Run Binary Trees with the Graal VM"""
  rbinarytrees(args, ['-XX:-BootstrapGraal'], 'graal')

def rspectralnormServer(args):
  """Run Spectral Norm with the HotSpot server VM"""
  rspectralnorm(args, [], 'server')

def rspectralnormGraal(args):
  """Run Spectral Norm with the Graal VM"""
  rspectralnorm(args, ['-XX:-BootstrapGraal'], 'graal')

def rbinarytreesGraal(args):
  """Run Spectral Norm with the Graal VM"""
  rspectralnorm(args, ['-XX:-BootstrapGraal'], 'graal')

def rnbodyServer(args):
  """Run NBody with the HotSpot server VM"""
  rnbody(args, [], 'server')

def rnbodyGraal(args):
  """Run NBody with the Graal VM"""
  rnbody(args, ['-XX:-BootstrapGraal'], 'graal')

def rfastaServer(args):
  """Run Fasta with the HotSpot server VM"""
  rfasta(args, [], 'server')

def rfastaGraal(args):
  """Run Fasta with the Graal VM"""
  rfasta(args, ['-XX:-BootstrapGraal'], 'graal')

def rfastareduxServer(args):
  """Run Fastaredux with the HotSpot server VM"""
  rfastaredux(args, [], 'server')

def rfastareduxGraal(args):
  """Run Fastaredux with the Graal VM"""
  rfastaredux(args, ['-XX:-BootstrapGraal'], 'graal')

def rpidigitsServer(args):
  """Run Pidigits with the HotSpot server VM"""
  rpidigits(args, [], 'server')

def rpidigitsGraal(args):
  """Run Pidigits with the Graal VM"""
  rpidigits(args, ['-XX:-BootstrapGraal'], 'graal')

def rregexdnaServer(args):
  """Run Regexdna with the HotSpot server VM"""
  rregexdna(args, [], 'server')

def rregexdnaGraal(args):
  """Run Regexdna with the Graal VM"""
  rregexdna(args, ['-XX:-BootstrapGraal'], 'graal')

def rmandelbrotServer(args):
  """Run Mandelbrot with the HotSpot server VM"""
  rmandelbrot(args, [], 'server')

def rmandelbrotGraal(args):
  """Run Mandelbrot with the Graal VM"""
  rmandelbrot(args, ['-XX:-BootstrapGraal'], 'graal')

def rreversecomplementServer(args):
  """Run Reversecomplement with the HotSpot server VM"""
  rreversecomplement(args, [], 'server')

def rreversecomplementGraal(args):
  """Run Reversecomplement with the Graal VM"""
  rreversecomplement(args, ['-XX:-BootstrapGraal'], 'graal')

def rknucleotideServer(args):
  """Run Knucleotide with the HotSpot server VM"""
  rknucleotide(args, [], 'server')

def rknucleotideGraal(args):
  """Run Knucleotide with the Graal VM"""
  rknucleotide(args, ['-XX:-BootstrapGraal'], 'graal')

def rallbenchmarksServer(args):
  """Run all benchmarks with the HotSpot server VM"""
  rbenchmarks(args, [], 'server')

def rallbenchmarksGraal(args):
  """Run all benchmarks with the Graal VM"""
  rbenchmarks(args, ['-XX:-BootstrapGraal'], 'graal')

def runittestServer(args):
  """Run unit tests with the HotSpot server VM"""
  runittest(args, [], 'server')

def runittestGraal(args):
  """Run unit tests with the Graal VM"""
  runittest(args, ['-XX:-BootstrapGraal'], 'graal')

# ------------------

def rbenchmarks(args, vmArgs, vm):
  rbinarytrees(args, vmArgs, vm)
  rfannkuch(args, vmArgs, vm)
  rfasta(args, vmArgs, vm)  
  rfastaredux(args, vmArgs, vm)  
  rknucleotide(args, vmArgs, vm)  
  rmandelbrot(args, vmArgs, vm)  
  rnbody(args, vmArgs, vm)  
  rpidigits(args, vmArgs, vm)  
  rregexdna(args, vmArgs, vm)
  rreversecomplement(args, vmArgs, vm)  
  rspectralnorm(args, vmArgs, vm)  

def rconsole(vmArgs, vm, cArgs):
  """Run R Console with the given VM"""
  global gvmOut

  extraArgs = [];
#  extraArgs = ['-XX:-Inline'];
#  extraArgs = ['-verbose:gc'];
#  extraArgs = ['-XX:+PrintGCDetails','-XX:+PrintHeapAtGC','-XX:+PrintHeapAtGCExtended'];
#  extraArgs = ['-Xmx6500m', '-verbose:gc'];
#  extraArgs = ['-Xmx2g', '-verbose:gc'];
  extraArgs = ['-Xmx4g'];
  gmod.vm( vmArgs + extraArgs + ['-cp', mx.classpath("fastr") , 'r.Console' ] + cArgs, vm = vm, out = gvmOut); 

def rshootout(args, vmArgs, vm, benchDir, benchFile, defaultArg):
  """Run given shootout benchmark using given VM"""

  if (len(args)==0):
    arg = defaultArg
  else:
    arg = args[0]

  source = join(os.getcwd(), "..", "fastr", "test", "r", "shootout", benchDir, benchFile)

#  rconsole(vmArgs + ['-XX:-Inline'], vm, ['--waitForKey', '-f', source, '--args', arg]);
#  rconsole(vmArgs, vm, ['--waitForKey', '-f', source, '--args', arg]);
  rconsole(vmArgs, vm, ['-f', source, '--args', arg]);

def stripTrailingNULL(fname):
  """Remove last 5 bytes from a file (the trailing NULL\n)"""

  sinfo = os.stat(fname)
  size = sinfo.st_size
  file = open(fname, "r");
  data = file.read(size - 5)
  file.close() 
  file = open(fname, "w")
  file.write(data)
  file.close()
  
def getFastaOutput(size):
  """Run Fasta capturing the output to a file"""
  
  fname = ".tmp.fasta." + size + ".out";
  if not os.path.exists(fname):
    print("Generating input for Regexdna, size " + size + "...\n");
    outputFile = open(fname, "w")
    def out(line): 
      outputFile.write(line)
    global gvmOut 
    gvmOut = out
    rfastaServer([size])
    gvmOut = None
    outputFile.close()
    print("Done.\n")

  stripTrailingNULL(fname)
  return fname


def rshootoutFastaInput(args, vmArgs, vm, benchDir, benchFile, defaultArg):
  """Run given shootout benchmark (that takes fasta output as input) using given VM"""

  if (len(args)==0):
    arg = defaultArg
  else:
    arg = args[0]

  input = getFastaOutput(arg) 
  source = join(os.getcwd(), "..", "fastr", "test", "r", "shootout", benchDir, benchFile)

#  rconsole(vmArgs + ['-XX:-Inline'], vm, ['--waitForKey','-f', source, '--args', input]);
#  rconsole(vmArgs, vm, ['--waitForKey', '-f', source, '--args', input]);
  rconsole(vmArgs, vm, ['-f', source, '--args', input]);

# ------------------

def rfannkuch(args, vmArgs, vm):
  """Run Fannkuch benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "fannkuch", "fannkuchredux.r", "9");

def rbinarytrees(args, vmArgs, vm):
  """Run Binary Trees benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "binarytrees", "binarytrees.r", "14");

def rspectralnorm(args, vmArgs, vm):
  """Run Spectral Norm benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "spectralnorm", "spectralnorm.r", "550");

def rnbody(args, vmArgs, vm):
  """Run NBody benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "nbody", "nbody.r", "200000");

def rfasta(args, vmArgs, vm):
  """Run Fasta benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "fasta", "fasta.r", "500000");

def rfastaredux(args, vmArgs, vm):
  """Run Fastaredux benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "fastaredux", "fastaredux.r", "1000000");

def rpidigits(args, vmArgs, vm):
  """Run Pidigits benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "pidigits", "pidigits.r", "220");

def rknucleotide(args, vmArgs, vm):
  """Run Knucleotide benchmark using the given VM"""
  rshootoutFastaInput(args, vmArgs, vm, "knucleotide", "knucleotide.r", "23000");

def rregexdna(args, vmArgs, vm):
  """Run Regexdna benchmark using the given VM"""
  rshootoutFastaInput(args, vmArgs, vm, "regexdna", "regexdna.r", "2500000");

def rreversecomplement(args, vmArgs, vm):
  """Run Reversecomplement benchmark using the given VM"""
  rshootoutFastaInput(args, vmArgs, vm, "reversecomplement", "reversecomplement.r", "2500000");

# -----------

def rmandelbrot(args, vmArgs, vm):
  """Run Mandelbrot benchmark using the given VM"""
  
  output = ".tmp.mandelbrot.out";
  outputFile = open(output, "w")
  def out(line): 
    outputFile.write(line)
  global gvmOut 
  gvmOut = out
  rshootout(args, vmArgs, vm, "mandelbrot", "mandelbrot.r", "6000");
  gvmOut = None
  outputFile.close()
  # note - the output file contains "NULL\n" at the end, something that should not be there
  stripTrailingNULL(output);
  
  file = open(output, "r");
  data = file.read()
  file.close() 
  hash = hashlib.md5(data).hexdigest()
  print "Binary output has MD5 hash ", hash, "\n"
  
# ----------

def runittest(args, vmArgs, vm): 
  """Run unit tests using the given VM""" 
  
  classes = gmod._find_classes_with_annotations(mx.project('fastr'), None, ['@Test', '@Parameters'])
  print "Classes are: ", classes
  gmod.vm( ['-esa', '-ea', '-cp', mx.classpath('fastr')] + vmArgs + ['org.junit.runner.JUnitCore'] + classes, vm )
  

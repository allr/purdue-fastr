
import os, sys, shutil;
from os.path import exists, join;
import mx;

# Graal VM module
gmod = None;

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
      'rspectralnorm': [rspectralnormServer, '[size]'],      
      'runittest': [runittestServer, ''],
      'rgunittest': [runittestGraal, '']
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
  rconsole([], 'server', [])
  
def rconsoleGraal(args):
  """Run R Console with the Graal VM"""  
  rconsole(['-XX:-BootstrapGraal'], 'graal', [])

def rconsoleDebugGraal(args):
  """Run R Console with the Graal VM, debugging options"""  
  rconsole(['-XX:-BootstrapGraal', '-G:+DumpOnError', '-G:Dump=Truffle', '-G:+PrintBinaryGraphs', '-G:+PrintCFG', '-esa'], 'graal', [])

def rfannkuchServer(args):
  """Run Fannkuch with the HotSpot server VM"""  
  rfannkuch(args, [], 'server')

def rfannkuchGraal(args):
  """Run Fannkuch with the Graal VM"""  
  rfannkuch(args, ['-XX:-BootstrapGraal'], 'graal')

def rbinarytreesServer(args):
  """Run Binary Trees with the HotSpot server VM"""  
  rbinarytrees(args, [], 'server')

def rspectralnormServer(args):
  """Run Spectral Norm with the HotSpot server VM"""  
  rspectralnorm(args, [], 'server')

def runittestServer(args):
  """Run unit tests with the HotSpot server VM"""
  runittest(args, [], 'server')

def runittestGraal(args):
  """Run unit tests with the Graal VM"""
  runittest(args, ['-XX:-BootstrapGraal'], 'graal')

# ------------------
  
def rconsole(vmArgs, vm, cArgs):
  """Run R Console with the given VM"""
  gmod.vm( vmArgs + ['-cp', mx.classpath("fastr") , 'r.Console' ] + cArgs, vm = vm ); 

def rfannkuch(args, vmArgs, vm):
  """Run Fannkuch benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "fannkuch", "fannkuchredux.r", "10L");

def rbinarytrees(args, vmArgs, vm):
  """Run Binary Trees benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "binarytrees", "binarytrees.r", "15L");

def rspectralnorm(args, vmArgs, vm):
  """Run Spectral Norm benchmark using the given VM"""
  rshootout(args, vmArgs, vm, "spectralnorm", "spectralnorm.r", "800L");

# generic shootout runner
def rshootout(args, vmArgs, vm, benchDir, benchFile, defaultArg):
  """Run given shootout benchmark using given VM"""
  if (len(args)==0):
    arg = defaultArg;
  else:
    arg = args[0];

  source = join(os.getcwd(), "..", "fastr", "test", "r", "shootout", benchDir, benchFile); 
  tmp = ".tmp." + benchDir + ".torun.r";

  shutil.copyfile(source, tmp);
  with open(tmp, "a") as f:
    f.write("run(" + arg + ")\n");       

  print("Argument "+ arg);
  print("Input file "+tmp);
  
#  rconsole(vmArgs + ['-XX:-Inline'], vm, ['--waitForKey',tmp]);
  rconsole(vmArgs, vm, ['--waitForKey',tmp]);
#  rconsole(vmArgs, vm, [tmp]);

# TODO: a generic function to run a shootout benchmark


def runittest(args, vmArgs, vm): 
  """Run unit tests using the given VM""" 
  
  classes = gmod._find_classes_with_annotations(mx.project('fastr'), None, ['@Test', '@Parameters'])
  gmod.vm( ['-esa', '-ea', '-cp', mx.classpath('fastr')] + vmArgs + ['org.junit.runner.JUnitCore'] + classes, vm )
  
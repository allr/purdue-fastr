
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
      'rfannkuch': [rfannkuchServer, '[size]'],
      'rgfannkuch': [rfannkuchGraal, '[size]']
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

def rfannkuchServer(args):
  """Run Fannkuch with the HotSpot server VM"""  
  rfannkuch(args, [], 'server')

def rfannkuchGraal(args):
  """Run Fannkuch with the Graal VM"""  
  rfannkuch(args, ['-XX:-BootstrapGraal'], 'graal')

# ------------------
  
def rconsole(vmArgs, vm, cArgs):
  """Run R Console with the given VM"""
  gmod.vm( vmArgs + ['-cp', mx.classpath("fastr") , 'r.Console' ] + cArgs, vm = vm ); 

def rfannkuch(args, vmArgs, vm):
  """Run Fannkuch benchmark using the given VM"""
  if (len(args)==0):
    size = '6L';
  else:
    size = args[0] + 'L';

  source = join(os.getcwd(), "..", "fastr", "test", "r", "shootout", "fannkuch", "fannkuchredux.r"); 
  tmp = ".tmp.fannkuch.torun.r";

  shutil.copyfile(source, tmp);
  with open(tmp, "a") as f:
#  
#  This was intended as a warm-up, but running with different sizes breaks
#  Truffle/Graal: it gives incorrect results
#
#    f.write("fannkuch(3L)\n");
#    f.write("fannkuch(3L)\n");   
#    f.write("fannkuch(3L)\n");     
    f.write("fannkuch("+size+")\n");
    f.write("fannkuch("+size+")\n");
    f.write("fannkuch("+size+")\n");    
    f.write("fannkuch("+size+")\n");       
  print("Problem size "+size);
  print("Input file "+tmp);
  
  rconsole(vmArgs, vm, [tmp]);
  
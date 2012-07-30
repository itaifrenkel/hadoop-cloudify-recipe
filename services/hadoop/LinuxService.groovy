import org.cloudifysource.dsl.utils.ServiceUtils;

class LinuxService {

def name

LinuxService(name) {
	this.name = name
}

boolean isRuning() {
  serviceCmd("${name} status").exitcode == 0;
}

void start () {
  serviceCmdThrowOnExitCode("${name} start"); 
}

void stop() {
  serviceCmdThrowOnExitCode("${name} stop"); 
}

/**
 * Returns an object with the following properties
 * stdout, stderr, and exitcode
 */
static def serviceCmd(args)  {
      def ant = new AntBuilder()
      ant.exec(outputproperty:"stdout", 
 		       errorproperty:"stderr",
             resultproperty:"exitcode",
             executable: "service") {
                arg(line:args)
             }
	  def ret = ant.project.properties;
	  ret.exitcode = ret.exitcode as int;
      return ret;
}

/**
 * Returns an object with the following properties
 * stdout, stderr
 * exitcode - is always 0, since throws exception if not zero
 */

static def serviceCmdThrowOnExitCode(args)  {
	def ret = serviceCmd(args)
	  if (ret.exitcode != 0) {
         throw new Exception("command \"service ${args}\" exit code ${ret.exitcode} stdout: ${ret.stdout} stderr: ${ret.stderr}")
       }
	return ret;
}

}
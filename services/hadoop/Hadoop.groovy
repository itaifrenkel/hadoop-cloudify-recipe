import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory;
import org.cloudifysource.dsl.context.ServiceContext;

class Hadoop {

def static boolean isNameNodeRuning(){
  def ret = serviceCmd("hadoop-hdfs-namenode status");
  return ret.exitcode == 0
}

def static void install() {

}

static void start () {
  startNameNode();
  //startDataNode();
  //startSecondaryNode();  
}

static void stop() {
  //stopDataNode();
  //stoptSecondaryNode();
  stopNameNode();
}

def static  startDataNode(){
  serviceCmdThrowOnExitCode("hadoop-hdfs-datanode start"); 
}

def static startNameNode(){
  def nameServicePort = "50070"
  def ipAddress = ServiceUtils.getPrimaryInetAddress();
  serviceCmdThrowOnExitCode("hadoop-hdfs-namenode start ${ipAddress}:${nameServicePort}")
   
}

def static startSecondaryNode(){
  serviceCmdThrowOnExitCode("hadoop-hdfs-secondarynamenode start");
}

def static stopDataNode(){
 serviceCmdThrowOnExitCode("hadoop-hdfs-datanode stop"); 
}

def static stopNameNode(){
 serviceCmdThrowOnExitCode("hadoop-hdfs-namenode stop");
}

def static stopSecondaryNode(){
  serviceCmdThrowOnExitCode("hadoop-hdfs-secondarynamenode stop");
}


/**
 * Returns an object with the following properties
 * stdout, stderr, and exitcode
 */
static def serviceCmd(args)  {
      def ant = new AntBuilder()
      ant.exec(outputproperty:"stdout", 
 		   errorproperty:"stderr"
             resultproperty:"exitcode",
             executable: "service") {
                arg(line:args)
             }
      return ant.project.properties;
}

static def serviceCmdThrowOnExitCode(args)  {
	def ret = serviceCmd(args)
	  if (ret.exitcode != 0) {
         throw new Exception("command \"service ${args}\" exit code ${ret.exitcode} stdout: ${ret.stdout} stderr: ${ret.stderr}")
       }
	return ret;
}

}
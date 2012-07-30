import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory;
import org.cloudifysource.dsl.context.ServiceContext;

class Hadoop {

def static workingDir
def static nameServicePort = "50070"
def static ip = ServiceUtils.getPrimaryInetAddress(); 
//def static context;
//def static config;
//def static installDir;

def static init(serviceContext) {
 
  //context = serviceContext
  println "Aftyer Context"
  //config = new ConfigSlurper().parse(new File("${context.serviceDirectory}/hadoop-service.properties").toURL())
  println "Aftyer Config"

  //installDir = "${System.properties["user.home"]}/.cloudify/${context.applicationName}_${context.serviceName}_${context.instanceId}"
  println "Aftyer installDir" 
  //workingDir = context.serviceDirectory
  nameServicePort = "50070"
    
}

def static boolean isNameNodeRuning(){
  def output =serviceCmd("hadoop- &hdfs-namenode status");
  return (output.contains("running") && output.contains("OK"));
}

def static void install() {

}

static void start () {
  if (isNameNodeRuning())
   stop();
  startNameNode();
  startDataNode();
  startSecondaryNode();
  
}

static void stop() {
  stopDataNode();
  stoptSecondaryNode();
  stopNameNode();
}

def static  startDataNode(){
  serviceCmd("hadoop-hdfs-datanode start"); 
}

def static startNameNode(){
  serviceCmd("hadoop-hdfs-namenode start ${ip}:${nameServicePort}")
   
}

def static startSecondaryNode(){
  serviceCmd("hadoop-hdfs-secondarynamenode start");
}

def static stopDataNode(){
 serviceCmd("hadoop-hdfs-datanode stop"); 
}

def static stopNameNode(){
 serviceCmd("hadoop-hdfs-namenode stop");
}

def static stopSecondaryNode(){
  serviceCmd("hadoop-hdfs-secondarynamenode stop");
}


static String serviceCmd(args)  {
      def ant = new AntBuilder()
      ant.exec(outputproperty:"cmdOut", //includes stdout and stderr
             resultproperty:"cmdExit",
             failonerror: "true",
             executable: "service") {
                arg(line:args)
             }
      String cmdOut = ant.project.properties.cmdOut
      Integer cmdExit = ant.project.properties.cmdExit 
      if (cmdExit != 0 && !args.contains("status")) {
        throw new Exception("command \"service ${args}\" exit code ${cmdExit} output: ${cmdOut}")
      }
      return cmdOut
}

}
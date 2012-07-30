import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory;
import org.cloudifysource.dsl.context.ServiceContext;

class Hadoop {

def static workingDir
def static nameServicePort = "50070"
def static ip = ServiceUtils.getPrimaryInetAddress(); 

def static init() {
 
  context = ServiceContextFactory.getServiceContext()
  println "Aftyer Context"
  config = new ConfigSlurper().parse(new File("${context.serviceDirectory}/hadoop-service.properties").toURL())
  println "Aftyer Config"

  installDir = "${System.properties["user.home"]}/.cloudify/${context.applicationName}_${context.serviceName}_${context.instanceId}"
  println "Aftyer installDir" 
  workingDir = context.serviceDirectory
  nameServicePort = "50070"
    
}

def static boolean isNameNodeRuning(){
  serviceCmd("hadoop-hdfs-namenode status").contains("running");
}

def static void install() {

}

static void start () {
 
  startNameNode();
  startDataNode();
  startSecondaryNode();
  
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


static String serviceCmd(args)  {
      ant = new AntBuilder()
      ant.exec(outputproperty:"cmdOut", //includes stdout and stderr
             resultproperty:"cmdExit",
             failonerror: "true",
             executable: "service") {
                arg(line:args)
             }
      cmdOut = ant.project.properties.cmdOut
      cmdExit = ant.project.properties.cmdExit
      if (cmdExit != 0) {
        throw new Exception("command \"service ${args}\" exit code ${cmdExit} output: ${cmdOut}")
      }
      return cmdOut
}

}
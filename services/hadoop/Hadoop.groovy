import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory;
import org.cloudifysource.dsl.context.ServiceContext;

class Hadoop {

def  workingDir
def  config
def  context
def ant

Hadoop() {
   println "Abefore ant"
  ant = new AntBuilder()
  println "Aftyer Ant"
  context = ServiceContextFactory.getServiceContext()
  println "Aftyer Context"
  config = new ConfigSlurper().parse(new File("${context.serviceDirectory}/hadoop-service.properties").toURL())
    println "Aftyer Config"

  installDir = "${System.properties["user.home"]}/.cloudify/${context.applicationName}_${context.serviceName}_${context.instanceId}"
   println "Aftyer installDir"
  ip = ServiceUtils.getPrimaryInetAddress(); 
  workingDir = context.serviceDirectory
  nameServicePort = "50070"
    
}

boolean isNameNodeRuning(){
  serviceCmd("hadoop-hdfs-namenode status").contains("running");
}

void install() {

}

void start () {
 
  startNameNode();
  startDataNode();
  startSecondaryNode();
  
}

void startDataNode(){
  serviceCmd("hadoop-hdfs-datanode start"); 
}

void startNameNode(){
  serviceCmd("hadoop-hdfs-namenode start ${ip}:${nameServicePort}")
   
}

void startSecondaryNode(){
  serviceCmd("hadoop-hdfs-secondarynamenode start");
}


def serviceCmd(args)  {
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
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.dsl.context.ServiceContextFactory;
import org.cloudifysource.dsl.context.ServiceContext;

class Hadoop {

String workingDir
ConfigObject config
ServiceContext context
AntBuilder ant

Hadoop() {
  ant = new AntBuilder()
  context = ServiceContextFactory.getServiceContext()
  config = new ConfigSlurper().parse(new File("${context.serviceDirectory}/hadoop-service.properties").toURL())

  installDir = "${System.properties["user.home"]}/.cloudify/${context.applicationName}_${context.serviceName}_${context.instanceId}"
  ip = ServiceUtils.getPrimaryInetAddress(); 
  workingDir = context.serviceDirectory
  nameServicePort = "50070"
    
}

nameServiceIsRuning(){
  return true;
}

install() {

}

start () {
 
  startNameNode();
  startDataNode();
  startSecondaryNode();
  
}

startDataNode(){
  cmd = "service hadoop-hdfs-datanode start") 
  ant.exec();
}

startNameNode(){
  cmd = "service hadoop-hdfs-namenode start ${ip}:${nameServicePort}"
  ant.exec(cmd)  
}

startSecondaryNode(){
  cmd = "service hadoop-hdfs-secondarynamenode start"
  ant.exec(cmd)  
}

isNameNodeRunning {
      ant = new AntBuilder()
      ant.exec(outputproperty:"cmdOut",
             resultproperty:"cmdExit",
             failonerror: "true",
             executable: "service") {
                arg("hadoop-hdfs-namenode")
                arg("status")
             }
  	  result = ant.project.properties.cmdOut
      result.contains("running")
}

serviceCommand(args) {
      ant = new AntBuilder()
      ant.exec(outputproperty:"cmdOut", //includes stdout and stderr
             resultproperty:"cmdExit",
             failonerror: "true",
             executable: "service") {
                arg(line:args)
             }
}

}
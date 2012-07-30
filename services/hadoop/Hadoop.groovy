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

isNameServiceRuning(){
  serviceCmd("hadoop-hdfs-namenode status").contains("running");
}

install() {

}

start () {
 
  startNameNode();
  startDataNode();
  startSecondaryNode();
  
}

startDataNode(){
  serviceCmd("hadoop-hdfs-datanode start"); 
}

startNameNode(){
  serviceCmd("hadoop-hdfs-namenode start ${ip}:${nameServicePort}"
   
}

startSecondaryNode(){
  serviceCmd("hadoop-hdfs-secondarynamenode start");
}


serviceCmd(args) {
      ant = new AntBuilder()
      ant.exec(outputproperty:"cmdOut", //includes stdout and stderr
             resultproperty:"cmdExit",
             failonerror: "true",
             executable: "service") {
                arg(line:args)
             }
}

}
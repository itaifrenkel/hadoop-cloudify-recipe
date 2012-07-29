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

  workingDir = context.serviceDirectory
}

install() {

}

start () {
  Thread.sleep(Long.MAX_VALUE);
}

}
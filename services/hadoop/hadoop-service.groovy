import java.util.concurrent.TimeUnit;
import static Hadoop.*

service {
	name "hadoop"
	icon "hadoop.jpg"
	type "NOSQL_DB"
	
  elastic false
	numInstances 1
	minAllowedInstances 1
	maxAllowedInstances 2
	//Hadoop.init()
	
	compute {
		template "SMALL_LINUX"
	}

	lifecycle {
		install { Hadoop.install() }
		start { println "befor start f"
			def context = ServiceContextFactory.getServiceContext()
			Hadoop.init(context)
			Hadoop.startNameNode() }
			startDetection {
    	//	ServiceUtils.isPortsOccupied(hadoop.nameServicePort, "127.0.0.1") &&
    		Hadoop.isNameNodeRuning()
	}
	}
	




	
}
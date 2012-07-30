import java.util.concurrent.TimeUnit;
import static Hadoop.*
import static JmxMonitors.*

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
		start { println "before start f"
			//def context = ServiceContextFactory.getServiceContext()
			Hadoop.init(context)
			Hadoop.start() }
		preStop {
			Hadoop.stop()
		}	
		startDetection {
    	//	ServiceUtils.isPortsOccupied(hadoop.nameServicePort, "127.0.0.1") &&
    		Hadoop.isNameNodeRuning()
		}
		
		monitors {
		
			def metricNamesToMBeansNames = [
				"Number of active metrics sources": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "NumActiveSources"],
				"Number of active metrics sinks": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "NumActiveSinks"],
				"Number of ops for snapshot stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "SnapshotNumOps"],
				"Average time for snapshot stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "SnapshotAvgTime"],
				"Number of ops for publishing stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "PublishNumOps"],
				"Average time for publishing stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "PublishAvgTime"],
				"Dropped updates by all sinks": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "DroppedPubAll"],
			]
			
			return getJmxMetrics("127.0.0.1",currJmxPort,metricNamesToMBeansNames)										
    	        }			

	}
	




	
}
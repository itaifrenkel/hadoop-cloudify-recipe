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
		start {
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
	
	userInterface {

		metricGroups = ([
			metricGroup {

				name "HadoopNameNodeStats"

				metrics([
					"Number of active metrics sources",
					"Number of active metrics sinks",
					"Number of ops for snapshot stats",
					"Average time for snapshot stats",
					"Number of ops for publishing stats",
					"Average time for publishing stats",
					"Dropped updates by all sinks",
				])
			} ,

		]
		)

		widgetGroups = ([
			widgetGroup {
				name "Number of active metrics sources"
				widgets ([
					balanceGauge{metric = "Number of active metrics sources"},
					barLineChart{
						metric "Number of active metrics sources"
						axisYUnit Unit.REGULAR
					}
				])
			},
			widgetGroup {
				name "Number of active metrics sinks"
				widgets([
					balanceGauge{metric = "Number of active metrics sinks"},
					barLineChart {
						metric "Number of active metrics sinks"
						axisYUnit Unit.REGULAR
					}
				])
			},
			widgetGroup {
				name "Number of ops for snapshot stats"
				widgets ([
					balanceGauge{metric = "Number of ops for snapshot stats"},
					barLineChart{
						metric "Number of ops for snapshot stats"
						axisYUnit Unit.REGULAR
					}
				])
			},
			widgetGroup {

				name "Average time for snapshot stats"
				widgets([
					balanceGauge{metric = "Average time for snapshot stats"},
					barLineChart {
						metric "Average time for snapshot stats"
						axisYUnit Unit.REGULAR
					}
				])
			} ,
			widgetGroup {

				name "Number of ops for publishing stats"
				widgets([
					balanceGauge{metric = "Number of ops for publishing stats"},
					barLineChart {
						metric "Number of ops for publishing stats"
						axisYUnit Unit.REGULAR
					}
				])
			} ,
			widgetGroup {

				name "Average time for publishing stats"
				widgets([
					balanceGauge{metric = "Average time for publishing stats"},
					barLineChart {
						metric "Average time for publishing stats"
						axisYUnit Unit.REGULAR
					}
				])
			} ,
			widgetGroup {

				name "Dropped updates by all sinks"
				widgets([
					balanceGauge{metric = "Dropped updates by all sinks"},
					barLineChart {
						metric "Dropped updates by all sinks"
						axisYUnit Unit.REGULAR
					}
				])
			} ,
		]
		)
	}

}
static import HDFS.hdfsCmdThrowOnExitCode as hdfsClient

service {
	name "hadoop"
	icon "hadoop.jpg"
	type "NOSQL_DB"

	def ipAddress = ServiceUtils.getPrimaryInetAddress();
	def namenode = new LinuxService("hadoop-hdfs-namenode")
	def datanode = new LinuxService("hadoop-hdfs-datanode")
	
	lifecycle {
		
		install { 
			
		}
		
		start {
			namenode.start("${ipAddress}:${nameNodeInfoPort}")
			datanode.start("${ipAddress}:${dataNodeInfoPort}")

			//dummy process
			return "sh -c :".execute() 
		}
		
		preStop {
			datanode.stop()
			namenode.stop()
		}	
		
		startDetection {
			def dataNodeInfoUrl="http://${ipAddress}:${dataNodeInfoPort}"			
			return
			namenode.isStarted() &&
			datanode.isStarted() &&
			ServiceUtils.isHttpURLAvailable(dataNodeInfoUrl)
		}
		
        locator {     
		    ServiceUtils.ProcessUtils.getPidsWithQuery("Args.*.ew=org.apache.hadoop.hdfs.server.namenode.NameNode") +
			ServiceUtils.ProcessUtils.getPidsWithQuery("Args.*.ew=org.apache.hadoop.hdfs.server.datanode.DataNode")
        }

		monitors {
		
			def nameNodeJmxBeans = [
				"Number of active metrics sources": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "NumActiveSources"],
				"Number of active metrics sinks": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "NumActiveSinks"],
				"Number of ops for snapshot stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "SnapshotNumOps"],
				"Average time for snapshot stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "SnapshotAvgTime"],
				"Number of ops for publishing stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "PublishNumOps"],
				"Average time for publishing stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "PublishAvgTime"],
				"Dropped updates by all sinks": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "DroppedPubAll"],

				"Total Files": ["Hadoop:name=FSNamesystem,service=NameNode", "FilesTotal"],
				"Total Blocks": ["Hadoop:name=FSNamesystem,service=NameNode", "BlocksTotal"],
			]
			
			return JmxMonitors.getJmxMetrics("127.0.0.1",nameNodeJmxPort,nameNodeJmxBeans)
        }
	}

customCommands ([	
    "LS" : {folderName ->        
        def cmd1 =  "dfs -ls " + folderName
        def out1 = hdfsClient(cmd1).stdout 
        println(out1)
        return "\n" +out1
    },
    "PUT" :  {srcFolderName, dstFolderName->        
        def cmd2 =  "dfs -put " + srcFolderName + " " + dstFolderName
        def out2 = hdfsClient(cmd2).stdout
         println(out2)
        return "\n"+ out2
    },
    "CAT" :  {fileName->        
        def cmd3 =  "dfs -cat " + fileName
        def out3 = hdfsClient(cmd3).stdout
         println(out3)
        return "\n"+ out3
    },
])	


	
	/*customCommands ([
		"put" : {url,dest -> 
			return ("downloading ${url} to ${context.serviceDirectory}/tmp and then copying to hdfs ${dst}")*/

			/*
			def ant = new AntBuilder()
			ant.sequential {
				get(src:"${url}", dest:"${context.serviceDirectory}/tmp", skipexisting:false, failonerror:true)
				exec(
					resultproperty:"exitcode",
					executable: "hdfs dfs put ") {
							arg("-put ${context.serviceDirectory}/tmp ${dst}")
					 }
			}
			
			def ret = ant.project.properties
			if (!ret.exitcode) {
				return 1;
			}
			return (ret.exitcode as int)
			*/
//		}
//	])

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
			metricGroup {

				name "HadoopFSNameSystem"

				metrics([
					"Total Files",
					"Total Blocks",
				])
			} ,
		]
		)

		widgetGroups = ([
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
			widgetGroup {

				name "Total Files"
				widgets([
					balanceGauge{metric = "Total Files"},
					barLineChart {
						metric "Total Files"
						axisYUnit Unit.REGULAR
					}
				])
			} ,
			widgetGroup {

				name "Total Blocks"
				widgets([
					balanceGauge{metric = "Total Blocks"},
					barLineChart {
						metric "Total Blocks"
						axisYUnit Unit.REGULAR
					}
				])
			} ,
		]
		)
	}
}
import static HDFS.hdfsClient

service {
	name "hadoop"
	icon "hadoop.jpg"
	type "NOSQL_DB"

	def ipAddress = ServiceUtils.getPrimaryInetAddress();
	def namenode = new LinuxService("hadoop-hdfs-namenode")
	def datanode = new LinuxService("hadoop-hdfs-datanode")
	
	lifecycle {
		
		install "install.sh"
		
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
				"Total Files": ["Hadoop:name=FSNamesystem,service=NameNode", "FilesTotal"],
				"Total Blocks": ["Hadoop:name=FSNamesystem,service=NameNode", "BlocksTotal"],
				"Capacity Used (GB)": ["Hadoop:name=FSNamesystem,service=NameNode", "CapacityUsedGB"],
				"Blocks with corrupt replicas": ["Hadoop:name=FSNamesystem,service=NameNode", "CorruptBlocks"],

				"Number of active metrics sources": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "NumActiveSources"],
				"Number of active metrics sinks": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "NumActiveSinks"],
				"Number of ops for snapshot stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "SnapshotNumOps"],
				"Average time for snapshot stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "SnapshotAvgTime"],
				"Number of ops for publishing stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "PublishNumOps"],
				"Average time for publishing stats": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "PublishAvgTime"],
				"Dropped updates by all sinks": ["Hadoop:name=MetricsSystem,service=NameNode,sub=Stats", "DroppedPubAll"],

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
     "COPY" :  {srcFolderName, dstFolderName->        
        def cmd4 =  "dfs -cp " + srcFolderName + " " + dstFolderName
        def out4 = hdfsClient(cmd4).stdout
         println(out4)
        return "\n"+ out4
    },
     "RM" :  {fileName->        
        def cmd5 =  "dfs -rmr " + fileName
        def out5 = hdfsClient(cmd5).stdout
         println(out5)
        return "\n"+ out5
    },
])	


	
	

	userInterface {

		metricGroups = ([
			metricGroup {

				name "FSNameSystem"

				metrics([
					"Total Files",
					"Total Blocks",
					"Capacity Used (GB)",
					"Blocks with corrupt replicas",
				])
			} ,
			metricGroup {

				name "NameNode Stats"

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
			widgetGroup {

				name "Capacity Used (GB)"
				widgets([
					balanceGauge{metric = "Capacity Used (GB)"},
					barLineChart {
						metric "Capacity Used (GB)"
						axisYUnit Unit.REGULAR
					}
				])
			} ,
			widgetGroup {

				name "Blocks with corrupt replicas"
				widgets([
					balanceGauge{metric = "Blocks with corrupt replicas"},
					barLineChart {
						metric "Blocks with corrupt replicas"
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
import java.util.concurrent.TimeUnit;

service {
	name "hadoop"
	icon "hadoop.jpg"
	type "NOSQL_DB"
	
  elastic false
	numInstances 1
	minAllowedInstances 1
	maxAllowedInstances 2
	def hadoopx = null;
	def hadoop = {
		if (hadoopx == null)
			hadoopx =new Hadoop()
	
		return hadoopx
		
	}
	
	compute {
		template "SMALL_LINUX"
	}

	lifecycle {
		install { hadoop().install() }
		start { hadoop().startNameNode() }
	}
	

	startDetection {
    	//	ServiceUtils.isPortsOccupied(hadoop.nameServicePort, "127.0.0.1") &&
    		hadoop().isNameServiceRuning()
	}


	
}
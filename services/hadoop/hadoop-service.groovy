import java.util.concurrent.TimeUnit;

service {
	name "hadoop"
	icon "hadoop.jpg"
	type "NOSQL_DB"
	
  elastic false
	numInstances 1
	minAllowedInstances 1
	maxAllowedInstances 2
	
	hadoop = new Hadoop();
	
	compute {
		template "SMALL_LINUX"
	}

	lifecycle {
		install { hadoop.install() }
		start { hadoop.startNameNode() }
	}
	

	startDetection {
    	//	ServiceUtils.isPortsOccupied(hadoop.nameServicePort, "127.0.0.1") &&
    		hadoop.isNameServiceRuning()
	}


	
}
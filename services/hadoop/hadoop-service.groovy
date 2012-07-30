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
	def hadoopx = null;
	def hadoop = {
		println "in hadoop f"
		if (hadoopx == null)
			hadoopx =new Hadoop()
	
		return hadoopx
		
	}
	
	compute {
		template "SMALL_LINUX"
	}

	lifecycle {
		install { hadoop().install() }
		start { println "befor start f"
			hadoop().startNameNode() }
			startDetection {
    	//	ServiceUtils.isPortsOccupied(hadoop.nameServicePort, "127.0.0.1") &&
    		hadoop().isNameNodeRuning()
	}
	}
	




	
}
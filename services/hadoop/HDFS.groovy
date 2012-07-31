class HDFS {

  static def hdfsCmd(args)  {
        def ant = new AntBuilder()
        ant.exec(outputproperty:"stdout", 
             errorproperty:"stderr",
               resultproperty:"exitcode",
               executable: "hdfs") {
                  arg(line:args)
               }
      def ret = ant.project.properties;
      ret.exitcode = ret.exitcode as int;
        return ret;
  }

  static def hdfsCmdThrowOnExitCode(args)  {
    def ret = hdfsCmd(args)
      if (ret.exitcode != 0) {
           throw new Exception("command \"hdfs ${args}\" exit code ${ret.exitcode} stdout: ${ret.stdout} stderr: ${ret.stderr}")
         }
    return ret;
  }

}
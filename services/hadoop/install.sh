#!/bin/bash

# args:
# $1 the error code of the last command (should be explicitly passed)
# $2 the message to print in case of an error
# 
# an error message is printed and the script exists with the provided error code
function error_exit {
	echo "$2 : error code: $1"
	exit ${1}
}

export PATH=$PATH:/usr/sbin:/sbin:/usr/bin || error_exit $? "Failed on: export PATH=$PATH:/usr/sbin:/sbin"


sudo yum -y -q update || error_exit $? "Failed on: sudo yum -y -q update"
cd /opt || error_exit $? "Failed on: cd /opt"
wget http://archive.cloudera.com/cdh4/one-click-install/redhat/6/x86_64/cloudera-cdh-4-0.noarch.rpm || error_exit $? "Failed on: wget http://archive.cloudera.com/cdh4/one-click-install/redhat/6/x86_64/cloudera-cdh-4-0.noarch.rpm"
sudo yum --nogpgcheck localinstall cloudera-cdh-4-0.noarch.rpm || error_exit $? "Failed on: sudo yum --nogpgcheck localinstall cloudera-cdh-4-0.noarch.rpm"
sudo rpm --import http://archive.cloudera.com/cdh4/redhat/6/x86_64/cdh/RPM-GPG-KEY-cloudera  || error_exit $? "Failed on: sudo rpm --import http://archive.cloudera.com/cdh4/redhat/6/x86_64/cdh/RPM-GPG-KEY-cloudera"
sudo yum install hadoop-0.20-conf-pseudo || error_exit $? "Failed on: sudo yum install hadoop-0.20-conf-pseudo"
echo '#! /bin/bash' && echo 'JAVA_HOME=$JHOME' && echo 'export JAVA_HOME' |  tee -a /etc/hadoop/conf/hadoop-env.sh /etc/hadoop/conf.pseudo.mr1/hadoop-env.sh || error_exit $? "Failed on: echo '#! /bin/bash' && echo 'JAVA_HOME=$JHOME' && echo 'export JAVA_HOME' |  tee -a /etc/hadoop/conf/hadoop-env.sh /etc/hadoop/conf.pseudo.mr1/hadoop-env.sh"
chmod +x /etc/hadoop/conf/hadoop-env.sh /etc/hadoop/conf.pseudo.mr1/hadoop-env.sh || error_exit $? "Failed on: chmod +x /etc/hadoop/conf/hadoop-env.sh /etc/hadoop/conf.pseudo.mr1/hadoop-env.sh"
rpm -ql hadoop-0.20-conf-pseudo || error_exit $? "Failed on: rpm -ql hadoop-0.20-conf-pseudo"
sudo -u hdfs hdfs namenode -format || error_exit $? "Failed on: sudo -u hdfs hdfs namenode -format"
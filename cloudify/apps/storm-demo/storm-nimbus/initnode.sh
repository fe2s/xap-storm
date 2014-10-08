

SUDO=sudo
if [ $USER = root ]; then
	SUDO=""
fi

$SUDO yum -y install wget

if [ -z "$JAVA_HOME" ]; then export JAVA_HOME=$HOME/java; fi

if [ -f /usr/local/lib/libzmq.a ]; then
  exit 0
fi

$SUDO yum -y install pkgconfig gcc-c++ glibc-headers autoconf.noarch automake make libtool libuuid-devel git
if [ -f "/etc/issue" -a -n "`grep -i centos /etc/issue`" ]; then
  $SUDO rpm -Uvh http://repo.webtatic.com/yum/centos/5/latest.rpm
  $SUDO yum -y install --enablerepo=webtatic git-all
  $SUDO yum -y install pkgconfig e2fsprogs-devel
fi



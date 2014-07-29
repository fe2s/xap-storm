export PATH=$PATH:/home/user/java/bin/
echo 'export PATH=$PATH:/home/user/java/bin/:/tmp/install/apache-storm-0.9.2-incubating/bin/'  >> ~/.bash_profile || error_exit $? "Failed on: export PATH"
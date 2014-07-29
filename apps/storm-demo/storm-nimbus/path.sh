#!/bin/bash -x

export PATH=$PATH:/home/user/java/bin/
echo 'export PATH=$PATH:/home/user/java/bin/:${config.echopath}'  >> ~/.bash_profile || error_exit $? "Failed on: export PATH"
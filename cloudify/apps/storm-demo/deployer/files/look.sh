#!/bin/sh

sed -i "2i export LOOKUPLOCATORS=$LUS_IP_ADDRESS" setenv.sh || error_exit $? 113 "Failed updating setenv.sh"

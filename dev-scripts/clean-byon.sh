#!/bin/bash

#!/bin/bash
for i in 10.6.133.204 10.6.133.210 10.6.133.203 10.6.133.173 10.6.129.66 10.6.133.133 10.6.129.112 10.6.132.191 10.6.133.4 10.6.133.107
#for i in 10.6.129.216
do
echo "start cleaning $i"

ssh user@$i "sudo killall -9 java"
ssh user@$i "sudo rm -rf ~/gigaspaces"
ssh user@$i "sudo rm -rf ~/.gigaspaces"
ssh user@$i "sudo rm -rf ~/java"
ssh user@$i "sudo rm -rf /tmp/*"
ssh user@$i "sudo rm -f /var/log/spooler-* /var/log/maillog-* /var/log/secure-* /var/log/messages-* "

done
exit 0
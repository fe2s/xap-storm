/*******************************************************************************
* Copyright (c) 2014 GigaSpaces Technologies Ltd. All rights reserved
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
import util


service {
	//def maxinstances=context.isLocalCloud()?1:200

	name "xap-container"
	type "APP_SERVER"
	icon "xap.png"
	//elastic false
	numInstances 2
	minAllowedInstances 2
	maxAllowedInstances 3


    def serviceIP = "127.0.0.1"
    compute {
        template "${template}"
    }

	def instanceId=context.instanceId

	lifecycle{

        init {
            if (!context.isLocalCloud()) {
                serviceIP = context.getPrivateAddress()
            }
            context.attributes.thisInstance.service_ip = serviceIP
        }

		install "xap_install.groovy"

		start "xap_start.groovy"

    //    postStart "xap_postStart.groovy"

        preStop "xap_preStop.groovy"


		locator {
			uuid=context.attributes.thisInstance.uuid
			i=0
			while (uuid==null){
				Thread.sleep 1000
				uuid=context.attributes.thisInstance.uuid
				if (i>20){
					println "LOCATOR TIMED OUT"
					break
				}
				i=i+1
			}
			if(i>21)return null

			i=0
			def pids=[]
			while(pids.size()==0){
				pids=ServiceUtils.ProcessUtils.getPidsWithQuery("Args.*.ct=${uuid}");
				i++;
				if(i>20){
					println "PROCESS NOT DETECTED"
					break
				}
				Thread.sleep(1000)
			}
			return pids
		}
	}


	customCommands ([
//Public entry points

		"update-hosts": {String...line ->
			util.invokeLocal(context,"_update-hosts", [
				"update-hosts-hostsline":line
			])
		 },

		//Actual parameterized calls
		"_update-hosts"	: "commands/update-hosts.groovy"
	])


	userInterface {
		metricGroups = ([
		]
		)

		widgetGroups = ([
		]
		)
	}

    network {
        template "APPLICATION_NET"
        accessRules {
            incoming ([
                    accessRule {
                        type "APPLICATION"
                        portRange "${bindPort}"
                    }
            ])
        }
    }
}



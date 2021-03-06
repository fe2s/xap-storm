/*******************************************************************************
* Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
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
import static Shell.*;
import java.util.concurrent.TimeUnit
import groovy.text.SimpleTemplateEngine
import groovy.util.ConfigSlurper;
import java.net.InetAddress;

def context=null
try{
context = org.cloudifysource.dsl.context.ServiceContextFactory.getServiceContext()
}
catch(e){
context = org.cloudifysource.utilitydomain.context.ServiceContextFactory.getServiceContext()
}


config = new ConfigSlurper().parse(new File("storm-service.properties").toURL())

def service = null

while (service == null)
{
   println "Locating zookeeper service...";
   service = context.waitForService("zookeeper", 120, TimeUnit.SECONDS)
}
def zooks = null;
def rowCount=0;
while(zooks==null)
{
   println "Locating zookeeper service instances. Expecting " + service.getNumberOfPlannedInstances();
   zooks = service.waitForInstances(service.getNumberOfPlannedInstances(), 120, TimeUnit.SECONDS )
}

println "Found ${zooks.length} zookeeper nodes"

def nimbus = InetAddress.localHost.hostAddress
def hostName= InetAddress.localHost.hostName

def binding=["zooks":zooks,"nimbus":nimbus,"hostName":hostName]
def yaml = new File('templates/storm.yaml')
engine = new SimpleTemplateEngine()
template = engine.createTemplate(yaml).make(binding)

sh "chmod +x initnode.sh"
sh "./initnode.sh"

new AntBuilder().sequential {
mkdir(dir:"${config.installDir}")
}

def command = """wget -O ${config.installDir}/${config.zipName} ${config.downloadPath}"""		// Create the String
def proc = command.execute()                 // Call *execute* on the string
proc.waitFor()                               // Wait for the command to finish

// Obtain status and output
println "return code: ${ proc.exitValue()}"
println "stderr: ${proc.err.text}"
println "stdout: ${proc.in.text}" // *out* from the external program is *in* for groovy


new AntBuilder().sequential {
	get(src:"https://dl.dropboxusercontent.com/s/kc933u6vz2crqkb/storm-starter-0.0.1-SNAPSHOT-jar-with-dependencies.jar", dest:"commands", skipexisting:true)
	unzip(src:"${config.installDir}/${config.zipName}", dest:config.installDir, overwrite:true)
	
	//dos2unix on the linux script files
	fixcrlf(srcDir:"${config.installDir}/${config.name}/bin", eol:"lf", eof:"remove", excludes:"*.bat *.jar")
	//delete(file:"${config.installDir}/${config.zipName}")

   //templates start scripts
	chmod(file:"${config.installDir}/${config.name}/bin/storm", perm:'ugo+rx')
	chmod(dir:"${config.installDir}/${config.name}/bin", perm:'ugo+rx', includes:"*.sh")
	chmod(dir:"commands", perm:'ugo+rx', includes:"*.sh")
	delete(file:"${config.installDir}/${config.name}/conf/storm.yaml")


	//add host entry
	exec(executable:"commands/addhost.sh", osfamily:"unix") {
		arg(line:"${context.privateAddress} ${InetAddress.localHost.hostName}")
	}
}

new File("${config.installDir}/${config.name}/conf/storm.yaml").withWriter{ out->
  out.write(template.toString())
}


sh "chmod +x path.sh"
sh "./path.sh"

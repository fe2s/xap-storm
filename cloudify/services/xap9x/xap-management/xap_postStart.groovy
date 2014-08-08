import groovy.text.SimpleTemplateEngine
import org.cloudifysource.dsl.utils.ServiceUtils
import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import org.openspaces.admin.AdminFactory
import org.openspaces.admin.Admin
import java.util.concurrent.TimeUnit
import groovy.util.ConfigSlurper



context=ServiceContextFactory.serviceContext
config = new ConfigSlurper().parse(new File(context.serviceName+"-service.properties").toURL())
println "start of deploy space"
def look = context.attributes.thisApplication["xaplookuplocators"]
println "${look}"


///
/// FIND xap-container NODES
///
def service = null

while (service == null)
{
    println "Locating xap-container service...";
    service = context.waitForService("xap-container", 400, TimeUnit.SECONDS)
}
def xaps = null;
def rowCount=0;
while(xaps==null)
{
    println "Locating xap-container service instances. Expecting " + service.getNumberOfPlannedInstances();
    xaps = service.waitForInstances(service.getNumberOfPlannedInstances(), 400, TimeUnit.SECONDS )
}

println "Found ${xaps.length} xap-container nodes"

// Start space creation

def builder = new AntBuilder()
builder.sequential {
exec(executable:"${config.installDir}/${config.xapDir}/bin/gs.sh") {
    arg(line:"deploy -cluster total_members=1,0 ${config.space}")
    //arg(line:"deploy-space -cluster total_members=1,0 space")
    env(key:"LOOKUPLOCATORS", value:"${look}")

    }
}

println 'end of deploy space'


println 'Start of deploy rest'

///
/// FIND storm-nimbus NODES
///
 service = null

while (service == null)
{
    println "Locating storm-nimbus service...";
    service = context.waitForService("storm-nimbus", 240, TimeUnit.SECONDS)
}


// Start rest service depl.

builder = new AntBuilder()
builder.sequential {
    exec(executable:"${config.installDir}/${config.xapDir}/bin/gs.sh") {
        arg(line:"deploy ${config.rest}")
        env(key:"LOOKUPLOCATORS", value:"${look}")

    }
}

println 'End of deploy rest'


println 'Start of deploy feeder'

def lookuplocators = context.attributes.thisApplication["xaplookuplocators"]
def JAVA_HOME = "${System.getenv('HOME')}/java"
context.attributes.thisInstance["javaHome"] = JAVA_HOME as String



def command = """java -classpath ${config.feeder} com.gigaspaces.storm.googleanalytics.feeder.Main ${look}"""		// Create the String
def proc = command.execute()                 // Call *execute* on the string
proc.waitFor()                               // Wait for the command to finish

// Obtain status and output
println "return code: ${ proc.exitValue()}"
println "stderr: ${proc.err.text}"
println "stdout: ${proc.in.text}"
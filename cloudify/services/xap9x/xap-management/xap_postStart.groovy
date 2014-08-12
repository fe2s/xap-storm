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



/// waiting xap-container NODES

    println "Waiting for xap-container service..."
def statsService = context.waitForService(config.containerServiceName, 900, TimeUnit.SECONDS)
if (statsService == null) {
    throw new IllegalStateException("stats service not found.");

}

statsHostInstances = statsService.waitForInstances(statsService.numberOfPlannedInstances, 900, TimeUnit.SECONDS)

if (statsHostInstances == null) {
    throw new IllegalStateException("Stats service instances are not ready");
}

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


/// waiting storm-nimbus NODES

println "Waiting for nimbus service..."
def nimbService = context.waitForService("storm-nimbus", 1500, TimeUnit.SECONDS)
        if (nimbService == null) {
    throw new IllegalStateException("stats service not found.");
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

 /// waiting storm-nimbus NODES #2
println "Waiting for nimbus service..."
    def nimbServicesec = context.waitForService("storm-nimbus", 2000, TimeUnit.SECONDS)
        if (nimbServicesec == null) {
    throw new IllegalStateException("stats service not found.");
        }


def lookuplocators = context.attributes.thisApplication["xaplookuplocators"]
def JAVA_HOME = "${System.getenv('HOME')}/java"
context.attributes.thisInstance["javaHome"] = JAVA_HOME as String


builder = new AntBuilder()
builder.sequential {
    exec(executable:"./feeder.sh", osfamily:"unix") {

    }
}

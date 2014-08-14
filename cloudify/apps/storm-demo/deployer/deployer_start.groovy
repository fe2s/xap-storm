import groovy.text.SimpleTemplateEngine
import groovy.util.ConfigSlurper
import org.cloudifysource.dsl.utils.ServiceUtils
import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import org.openspaces.admin.AdminFactory
import org.openspaces.admin.Admin
import java.util.concurrent.TimeUnit
import org.openspaces.admin.gsa.GridServiceAgents
import org.openspaces.admin.application.config.ApplicationConfig
import org.openspaces.admin.pu.config.ProcessingUnitConfig


context=ServiceContextFactory.serviceContext
config = new ConfigSlurper().parse(new File(context.serviceName+"-service.properties").toURL())

def look = context.attributes.thisApplication["xaplookuplocators"]
println "${look}"

println "#1 start of deploy space"

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
        env(key:"LOOKUPLOCATORS", value:"${look}")

    }
}

println '#1 end of deploy space'

println '#2 end of deploy storm'

//def builder = new AntBuilder()
//builder.sequential {
//   exec(executable:"/tmp/install/apache-storm-0.9.2-incubating/bin/storm") {
//      arg(line:"jar commands/storm-topology-1.0-SNAPSHOT.jar com.gigaspaces.storm.googleanalytics.topology.GoogleAnalyticsTopology google ${lookuplocators}")

//}
//}


println '#2 end of deploy storm'



println '#3 Start of deploy rest'

println "Waiting for nimbus service..."
def nimbService = context.waitForService("storm-nimbus", 1500, TimeUnit.SECONDS)
if (nimbService == null) {
    throw new IllegalStateException("stats service not found.");
}

builder = new AntBuilder()
builder.sequential {
    exec(executable:"${config.installDir}/${config.xapDir}/bin/gs.sh") {
        arg(line:"deploy ${config.rest}")
        env(key:"LOOKUPLOCATORS", value:"${look}")

    }
}

println '#3 End of deploy rest'


println '#4 Start of deploy feeder'


def JAVA_HOME = "${System.getenv('HOME')}/java"
context.attributes.thisInstance["javaHome"] = JAVA_HOME as String


builder = new AntBuilder()
builder.sequential {
    exec(executable:"files/feeder.sh", osfamily:"unix") {
        arg(line:"${look}")
    }
}

println '#4 End of deploy feeder'
import groovy.text.SimpleTemplateEngine
import org.cloudifysource.dsl.utils.ServiceUtils
import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import org.openspaces.admin.AdminFactory
import org.openspaces.admin.Admin
import java.util.concurrent.TimeUnit
import groovy.util.ConfigSlurper

context=ServiceContextFactory.serviceContext

println "START STORM DEPLOY"
///
/// FIND xap-management NODES
///
/// waiting xap-container NODES

println "Waiting for xap-container service..."
def xapService = context.waitForService(config.containerServiceName, 1500, TimeUnit.SECONDS)
if (xapService == null) {
    throw new IllegalStateException("xap-man. service not found.");

}

def lookuplocators = context.attributes.thisApplication["xaplookuplocators"]
println "lookup server ip is: ${lookuplocators}"

// Start storm deploy proc.

def builder = new AntBuilder()
builder.sequential {
  exec(executable:"/tmp/install/apache-storm-0.9.2-incubating/bin/storm") {
       arg(line:"jar commands/storm-topology-1.0-SNAPSHOT.jar com.gigaspaces.storm.googleanalytics.topology.GoogleAnalyticsTopology google ${lookuplocators}")

    }
}

println "END STORM DEPLOY"




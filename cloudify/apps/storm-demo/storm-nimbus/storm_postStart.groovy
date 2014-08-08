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
def service = null

while (service == null)
{
    println "Locating xap-management service...";
    service = context.waitForService("xap-management", 400, TimeUnit.SECONDS)
}

println "Found xap-management nodes!!"

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




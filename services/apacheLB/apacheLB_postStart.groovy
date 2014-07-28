import groovy.text.SimpleTemplateEngine
import groovy.util.ConfigSlurper;
import org.cloudifysource.dsl.utils.ServiceUtils
import org.cloudifysource.utilitydomain.context.ServiceContextFactory

// xap installation pack 
context=ServiceContextFactory.serviceContext
config = new ConfigSlurper().parse(new File(context.serviceName+"-service.properties").toURL())

if (!context.isLocalCloud()) {
    new AntBuilder().sequential {
        mkdir(dir: "${config.installDir}")
        get(src: config.downloadPath, dest: "${config.installDir}/${config.zipName}", skipexisting: true)
        unzip(src: "${config.installDir}/${config.zipName}", dest: config.installDir, overwrite: true)
        chmod(dir: "${config.installDir}/${config.xapDir}/bin", perm: "+x", includes: "*.sh")
    	chmod(dir: "${config.installDir}/${config.xapDir}/tools/apache/", perm: "+x", includes: "*.sh")
    }


}

//def command = "export JAVA_HOME='/home/user/java'"// Create the String
//def proc = command.execute()                 // Call *execute* on the string
//proc.waitFor()

// Obtain status and output
//println "return code: ${ proc.exitValue()}"
//println "stderr: ${proc.err.text}"
//println "stdout: ${proc.in.text}"


 //command = """sudo ${config.installDir}/${config.xapDir}/tools/apache/apache-lb-agent.sh -apache /etc/httpd/ -conf-dir /etc/httpd/conf.d/ -apachectl /usr/sbin/apachectl -locators """// Create the String
 //proc = command.execute()                 // Call *execute* on the string
//proc.waitFor()                               // Wait for the command to finish

// Obtain status and output
//println "return code: ${ proc.exitValue()}"
//println "stderr: ${proc.err.text}"
//println "stdout: ${proc.in.text}"


def javaHome = "/home/user/java/"
println "vertx.groovy: javaHome is ${javaHome}"

context.attributes.thisInstance["javaHome"] = javaHome as String

def builder = new AntBuilder()
builder.sequential {

exec(executable:"sudo ${config.installDir}/${config.xapDir}/tools/apache/apache-lb-agent.sh") {
            arg(line:"-apache /etc/httpd/ -conf-dir /etc/httpd/conf.d/ -apachectl /usr/sbin/apachectl -locators")
            env(key:"JAVA_HOME", value:javaHome)
            
        }

	}
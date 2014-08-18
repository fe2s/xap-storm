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




//download xap distr.
context=ServiceContextFactory.serviceContext
config = new ConfigSlurper().parse(new File(context.serviceName+"-service.properties").toURL())


new AntBuilder().sequential {
    mkdir(dir: "${config.installDir}")
    get(src: config.downloadPath, dest: "${config.installDir}/${config.zipName}", skipexisting: true)
    unzip(src: "${config.installDir}/${config.zipName}", dest: config.installDir, overwrite: true)
    chmod(dir: "${config.installDir}/${config.xapDir}/bin", perm: "+x", includes: "*.sh")
    chmod(dir: "${context.serviceDirectory}/files/", perm: "+x", includes: "*.sh")
}




println "end of installing"
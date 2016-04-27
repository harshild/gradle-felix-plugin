package com.harshild.gradle.plugins.felix.tasks

import com.harshild.gradle.plugins.felix.util.BndWrapper
import com.harshild.gradle.plugins.felix.util.BundleUtils
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Dependency


class BuildBundlesTask extends BaseTask {
    final String CONFIG_TEMPLATE = """felix.log.level=%d
felix.auto.deploy.action=%s
org.osgi.service.http.port=%d
obr.repository.url=%s
%s
"""

    def String jar(Dependency dep) {
        return "${dep.name}-${dep.version}.jar"
    }
    
    def bundleProjects(rootProject) {
        rootProject.subprojects.findAll { project -> project.name != name }
    }	
    
    def copySubprojects(rootProject, target) {
        bundleProjects(rootProject).each { project ->
            ant.copy(file: "${project.buildDir.absolutePath}/libs/${project.name}-${project.version}.jar",
                todir: "$target")
        }
    }
    
    def copySubprojectsConfigResource(rootProject, target) {
        bundleProjects(rootProject).each { project ->
        if(new File("${project.buildDir.absolutePath}/../config/").exists())
            ant.copy(todir: "$target/../config"){
            	fileset(dir: "${project.buildDir.absolutePath}/../config/")
            }
        }
    }

    @TaskAction
    def build() {
        def bundles = project.configurations.felix.dependencies.collect { jar(it) }
        def felixMain = project.configurations.felixMain.dependencies.collect { jar(it) }
        def bundleDir = "$targetDir/bundle"
        def nonBundles = [ ] as Set
        project.configurations.felixMain.each {
            if(felixMain.contains(it.name)) {
                ant.copy(file: it.path, tofile: felixMainJar)
            }
        }
        project.configurations.felix.each {
            if(bundles.contains(it.name)) {
                def nonBundle = BundleUtils.notBundle( it )
                if ( nonBundle ) nonBundles << it
                else {
                    ant.copy(file: it.path, todir: bundleDir)
                }
            }
        }

        nonBundles.each { File file ->
            println(file.name)

                try {
                    BndWrapper.wrapNonBundle( file, bundleDir )
                } catch ( e ) {
                    println( "Unable to wrap ${file.name}" + e.message )
                }

        }

        def confDir = "$targetDir/conf"
        new File(confDir).mkdirs()
        new File("$confDir/config.properties").withWriter { w ->
          w.write(String.format(CONFIG_TEMPLATE,
                  project.extensions.felix.logLevel,
                  project.extensions.felix.deployActions,
                  project.extensions.felix.httpPort,
                  project.extensions.felix.repositoryUrl,
                  project.extensions.felix.propertiesString
          ))
        }
        copySubprojects(project, bundleDir)
        copySubprojectsConfigResource(project, bundleDir)
    }
}

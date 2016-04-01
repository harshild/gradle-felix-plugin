package com.harshil.gradle.plugins.felix.tasks

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
            ant.copy(file: "${project.name}/build/libs/${project.name}-${project.version}.jar",
                todir: "$target")
        }
    }

    @TaskAction
    def build() {
        def bundles = project.configurations.felix.dependencies.collect { jar(it) }
        def felixMain = project.configurations.felixMain.dependencies.collect { jar(it) }
        def bundleDir = "$targetDir/bundle"
        project.configurations.felixMain.each {
            if(felixMain.contains(it.name)) {
                ant.copy(file: it.path, tofile: felixMainJar)
            }
        }
        project.configurations.felix.each {
            if(bundles.contains(it.name)) {
                ant.copy(file: it.path, todir: bundleDir)
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
    }
}

package com.harshild.gradle.plugins.felix.tasks

import com.harshild.gradle.plugins.felix.util.BndWrapper
import com.harshild.gradle.plugins.felix.util.BundleUtils
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Dependency

class BuildBundlesTask extends BaseTask {

    final String OSGI_CORE = "org.osgi.core-4.2.0.jar"
    final String COMMONS_LANG = "commons-lang3-3.1.jar"
    final String CONFIG_TEMPLATE = """felix.log.level=%d
felix.auto.deploy.action=%s
org.osgi.service.http.port=%d
obr.repository.url=%s
%s
"""

    def String jar(Dependency dep) {
        return "${dep.name}-${dep.version}.jar"
    }

    def getProjectsToBeBundles(rootProject) {
        List<String> excludeP = new ArrayList<>();
        Set<Project> bundles = new ArrayList<>();
        project.extensions.felix.excludedProjects.each{ String a->
            excludeP.add(a);
        }

        rootProject.subprojects.each {Project project ->
            if(excludeP.size()>0)
                excludeP.each { ex ->
                    if(project.name != ex && project.name != name)
                        bundles.add(project)
                }
            else
                bundles.add(project)
        }
        bundles
    }

    def copySubProjects(rootProject, target) {
        def bundleProjectList = getProjectsToBeBundles(rootProject)
        bundleProjectList.each { project ->
            def bundle = new File("${project.buildDir.absolutePath}/libs/${project.archivesBaseName}-${project.version}.jar")
            BndWrapper.wrapNonBundle(bundle,"$target")
        }
    }

    def copySubProjectsConfigResource(rootProject, target) {
        getProjectsToBeBundles(rootProject).each { project ->
            if(new File("${project.buildDir.absolutePath}/../config/").exists())
                ant.copy(todir: "$target/../config"){
                    fileset(dir: "${project.buildDir.absolutePath}/../config/")
                }
        }
    }

    @TaskAction
    def build() {
        def bundles = getRuntimeDependencies()
        def felixMain = project.configurations.felixMain.dependencies.collect { jar(it) }
        def bundleDir = "$targetDir/bundle"
        def nonBundles = [ ] as Set
        project.configurations.felixMain.each {
            if(felixMain.contains(it.name)) {
                ant.copy(file: it.path, tofile: felixMainJar)
            }
        }
        bundles.each {
                def nonBundle = BundleUtils.notBundle( it )
                if ( nonBundle ) nonBundles << it
                else {
                    ant.copy(file: it.path, todir: bundleDir)
                }
        }

        nonBundles.each { File file ->
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
        copySubProjects(project, bundleDir)
        copySubProjectsConfigResource(project, bundleDir)
    }

    private Set<File> getRuntimeDependencies() {
        def runtimeDependencies = [ ] as Set
        runtimeDependencies.addAll(getFelixRuntimeDependencies())
        runtimeDependencies.addAll(getSubProjectCompileDependencies())
        runtimeDependencies
    }

    private Set<File> getSubProjectCompileDependencies() {
        def runtimeDependencies = [ ] as Set
        project.subprojects.each { sub ->
            runtimeDependencies.addAll(sub.configurations.compile.resolve())
        }
        runtimeDependencies
    }

    private Set<File> getFelixRuntimeDependencies() {
        project.configurations.felix.resolve()
    }
}

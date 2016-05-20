package com.harshild.gradle.plugins.felix.tasks

import com.harshild.gradle.plugins.felix.util.BndWrapper
import com.harshild.gradle.plugins.felix.util.BundleUtils
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Dependency

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


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

    def bundleProjects(rootProject) {
        List<String> excludeP = new ArrayList<>();
        Set<Project> bundles = new ArrayList<>();
        project.extensions.felix.excludedProjects.each{
            excludeP.add(it);
        }

        rootProject.subprojects.each {project ->
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
        List<String> filterDep = new ArrayList<>()
        filterDep.add(OSGI_CORE)
        filterDep.add(COMMONS_LANG)
        bundleProjects(rootProject).forEach{project->
            String temp = project.name+"-"+project.version+".jar"
            filterDep.add(temp)
        }
        bundleProjects(rootProject).each { project ->
            List<File> fileList = new ArrayList<>();
            project.configurations.compile.each { z ->
                if (!filterDep.contains(z.name)) {
                    fileList.add(new File(z.toString()))
                }
            }
            File baseProject = new File("${project.buildDir.absolutePath}/libs/${project.name}-${project.version}.jar")
            fileList.add(baseProject)

            new File("$target/../tmp/felix").mkdirs()

            def bundle = new File( "$target/../tmp/felix/${project.name}-${project.version}.jar" )

            BundleUtils.fatJar( fileList, bundle ) {
                ZipFile input, ZipOutputStream out, ZipEntry entry ->
                    String temp = project.name+"-"+project.version+".jar"
                    if ( (input.name.contains(temp) ||
                            (entry.name != 'META-INF/MANIFEST.MF' &&
                                    entry.name != 'OSGI-INF/serviceComponents.xml')
                    ) &&
                            !entry.isDirectory()) {
                        out.putNextEntry(entry)
                        out.write(input.getInputStream(entry).bytes)
                    }

            }

            BndWrapper.wrapNonBundle(bundle,"$target")
        }

       new File("$target/../tmp/felix").deleteDir()
       new File("$target/../tmp/felix").delete()
    }

    def copySubProjectsConfigResource(rootProject, target) {
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
}

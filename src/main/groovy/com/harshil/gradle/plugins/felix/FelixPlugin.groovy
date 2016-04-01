package com.harshil.gradle.plugins.felix

import com.harshil.gradle.plugins.felix.tasks.BuildBundlesTask
import com.harshil.gradle.plugins.felix.tasks.RunFelixTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class FelixPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply(JavaPlugin.class)
    	project.extensions.felix = new FelixPluginExtension()
    	project.repositories {
           mavenLocal()
           mavenCentral()
        }
        project.configurations {
          felix
          felixMain
        }
    	project.dependencies {      
          felixMain project.extensions.felix.mainArtifact
        }
        project.task('buildBundles', type: BuildBundlesTask)
        project.buildBundles.dependsOn {
          project.build
          project.subprojects.build
        }
        project.task('runFelix', type: RunFelixTask, dependsOn: ':buildBundles')
    }
}


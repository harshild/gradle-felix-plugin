package com.harshil.gradle.plugins.felix.tasks

import org.gradle.api.DefaultTask

class BaseTask extends DefaultTask {

    def String getTargetDir() {
        return "${project.buildDir}/felixMain"
    }

    def String getFelixMainJar() {
        return "$targetDir/bin/felix.jar"
    }
}

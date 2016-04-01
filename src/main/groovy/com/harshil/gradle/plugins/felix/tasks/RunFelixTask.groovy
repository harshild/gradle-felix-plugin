package com.harshil.gradle.plugins.felix.tasks

import org.gradle.api.tasks.TaskAction

class RunFelixTask extends BaseTask {

    @TaskAction
    def run() {
        ant.java(jar: felixMainJar, fork: true, dir: targetDir)
    }
}

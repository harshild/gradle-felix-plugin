package com.harshil.gradle.plugins.felix.tasks

import com.harshil.gradle.plugins.felix.util.JarRunner
import org.gradle.api.tasks.TaskAction

class RunFelixTask extends BaseTask {

    @TaskAction
    def run() {
        JarRunner jarRunner = new JarRunner();
        jarRunner.run(project.buildDir.absolutePath+"\\felixMain")
    }
}

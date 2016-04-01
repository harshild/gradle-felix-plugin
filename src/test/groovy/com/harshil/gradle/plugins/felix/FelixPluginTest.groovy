package com.harshil.gradle.plugins.felix

import org.junit.Test
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import com.harshil.gradle.plugins.felix.tasks.BuildBundlesTask
import com.harshil.gradle.plugins.felix.tasks.RunFelixTask
import static org.testng.Assert.*

class FelixPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'felix'

        assertTrue(project.extensions.felix instanceof FelixPluginExtension)

        assertTrue(project.tasks.buildBundles instanceof BuildBundlesTask)
        assertTrue(project.tasks.runFelix instanceof RunFelixTask)
    }
}

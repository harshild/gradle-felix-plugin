package com.harshild.gradle.plugins.felix

import org.junit.Test
import org.junit.Before
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import com.harshild.gradle.plugins.felix.tasks.BuildBundlesTask
import com.harshild.gradle.plugins.felix.tasks.RunFelixTask
import static org.testng.Assert.*

class FelixPluginTest {
    Project project ;
    @Before
    public void init(){
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.harshild.felix-run'
    }

    @Test
    public void itShouldAssFelixExtension() {


        assertTrue(project.extensions.felix instanceof FelixPluginExtension)

    }

    @Test
    public void itShouldActiveTasks() {

        assertTrue(project.tasks.buildBundles instanceof BuildBundlesTask)
        assertTrue(project.tasks.runFelix instanceof RunFelixTask)
    }

}

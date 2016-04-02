package com.harshild.gradle.plugins.felix

class FelixPluginExtension {
    int logLevel = 1
    String deployActions = 'install,start,update'
    int httpPort = 8080
    String repositoryUrl = 'http://felix.apache.org/obr/releases.xml'
    String mainArtifact = 'org.apache.felix:org.apache.felix.main:5.4.0'
    
    Map properties = [:]
    
    def getPropertiesString() {
       properties.collect { "$it.key=$it.value" }.join("\n")
    }
}

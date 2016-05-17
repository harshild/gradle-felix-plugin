package com.harshild.gradle.plugins.felix

class FelixPluginExtension {
    int logLevel = 1
    String deployActions = 'install,start,update'
    int httpPort = 8080
    String repositoryUrl = 'http://felix.apache.org/obr/releases.xml'
    String mainArtifact = 'org.apache.felix:org.apache.felix.main:5.4.0'
    List<String> excludedProjects = new ArrayList<>();

    Map properties = [:]

    def getPropertiesString() {
        properties.collect { "$it.key=$it.value" }.join("\n")
    }

    def excludeProjects(String... args){
        args.each {
            excludedProjects.add(it);
        }
    }

    def getExcludedProjects(){
        return excludedProjects;
    }
}

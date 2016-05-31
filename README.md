Gradle Felix Plugin
============================
[![Build Status](https://travis-ci.org/harshild/gradle-felix-plugin.svg?branch=master)](https://travis-ci.org/harshild/gradle-felix-plugin)

This is the Felix Plugin for the Gradle build system. This Plugin enables the creation of a apache felix framework installation to ready run a osgi bundle application.

Features
--------

Features of the Felix Plugin:

* Downloads all required apache felix framework bundles
* Configure, assemble and execute/run felix framework with all bundles
* Compiling and including of custom bundles into the felix framework
* Auto Wrap default OSGi bundle configuration for non-bundles mentioned with felix configuration 
* Easy and Flexible development
* Auto creation of Bundle for all Sub Projects which includes all compile time dependencies[**DISCONTINUED**]

Usage
-----
### Include in project ###

Just star the project and follow the procedure

To apply the plugin you have to add the plugin dependency in your project build file:

	buildscript {
	    repositories {
	       mavenCentral()
	    }
	    dependencies {
	        classpath group: 'com.harshil.gradle', name: 'felix-plugin', version: '1.3.1'
	    }
	}

        apply plugin 'com.harshild.felix-run'

### Tasks ###

 * buildBundles: builds the apache felix framework
 * runFelix: runs the felix framework

The felix framework will be created in the following directory:

	build/felixMain
	
Configuration files in ./config in each subprojects will be copied to following directory:

	build/felixMain/config

### Dependency configuration ###

This plugin defines a new dependency configuration "felix". Artifacts defined with this dependency configuration will be resolved and added with all transitive dependencies as bundle to the felix bundle directory.

In addition to this, it also uses BndWrapper to automatically Wrap OSGI to make a non-bundle dependency mentioned in felix configuration OSGI deploy-able.
 
	dependencies {
	  felix 'org.apache.felix:org.apache.felix.scr:1.6.0'
	  felix 'org.apache.felix:org.apache.felix.log:1.0.1'
	}

### Other configurations ###

To specify felix framework configurations use felix extension

    felix {
    
    }
    
#### Felix Configuration Properties####

|  Property Alias |  Property |Default Value|
|---|---|---|
|log|felix.log.level|1|
|httpPort|org.osgi.service.http.port|8080|
|deployActions|felix.auto.deploy.action|install,start,update|
|repositoryUrl|obr.repository.url|http://felix.apache.org/obr/releases.xml |

For any other configuration user can use
    
    properties.testProperty = '123'
which will add testProperty in Felix configuration file

#### Exclude Projects ####

If your multi-project structure has some projects that are not supposed to be deployed as bundle you may exclude them 

    felix{
        excludeProjects 'project1', 'project2'
    }

Most Updated Versions
---------------------

## STABLE RELEASE ##

    Latest Version -- 1.3.1

## BETA RELEASE ##
This version is available to all but is not stable. It usually contains **newest features**, which are tested working but may/may not give desired results always.

	Latest Version -- 1.3.5-BETA

You may log issues found in BETA release.

#### How to Use ####

To use simply update your gradle build script with above mentioned version.

######WORKING EXAMPLE : https://github.com/harshild/gradle-felix-example######

Contribution
------------

* Log issues, new features and enhancements
* For code changes, send a pull request to development branch

## HELP NEEDED IN AREAS ##

* Unit Tests

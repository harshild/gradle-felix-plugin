Gradle Felix Plugin
============================

This is the Felix Plugin for the Gradle build system. This Plugin enables
the creation of a apache felix framework installation to ready run a osgi bundle
application.

Features
--------

Features of the Felix Plugin:
* Downloads all required apache felix framework bundles
* Configure, assemble and execute/run felix framework with all bundles
* Compiling and including of custom bundles into the felix framework
* Auto creation of Bundles for all Sub Projects which includes all compile time dependencies
* Auto Wrap default OSGi bundle configuration for non-bundles mentioned with felix configuration 

Usage
-----
### Include in project ###


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

This plugin defines a new dependency configuration named "felix".
Artifacts defined with this dependency configuration will be downloaded and
added to the felix bundle directory.

This feature makes ist possible to configure the bundle composition of the
felix framework. This configuration assembles a felix framework with
scr and log as additional bundles:

	dependencies {
	  felix 'org.apache.felix:org.apache.felix.scr:1.6.0'
	  felix 'org.apache.felix:org.apache.felix.log:1.0.1'
	}

Most Updated Version (usally BETA Release)
---------

This version is available to all but is not stable. It usually contains newest features, which are tested working but may/may not give desired results always.

	Latest Version -- 1.3.3-BETA

You may log issues found in BETA release.

### HOW TO USE ###

To use simply update your gradle build script with above mentioned version.

Credits
-----

Build with reference to Felix Launcher plugin
 https://github.com/thomasvolk/gradle-felix-launcher-plugin.git

Gradle Felix Plugin
============================

This is the Felix Plugin for the Gradle build system. This Plugin enables
the creation of a apache felix framework installation to ready run a osgi bundle
application.

Features
--------

Features of the Felix Launcher Plugin:

* Download of the required apache felix framework bundles
* Configuration, assembly and execution of the felix framework
* Compiling and including of custom bundles into the felix framework

Usage
-----
### Include in project ###


To apply the plugin you have to add the plugin dependency in your project build file:

	buildscript {
	    repositories {
	        url 'https://github.com/harshild/repo/raw/master/'
	    }
	    dependencies {
	        classpath group: 'com.harshild.gradle', name: 'felix-plugin', version: '1.3.0'
	    }
	}

        apply plugin 'com.harshild.felix-run'

### Tasks ###

 * buildBundles: builds the apache felix framework
 * runFelix: runs the felix framework

The felix framework will be created in the following directory:

	build/felixMain

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

Credits
-----

Build with reference Felix Launcher plugin
 https://github.com/thomasvolk/gradle-felix-launcher-plugin.git

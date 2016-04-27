
package com.harshild.gradle.plugins.felix.util

import aQute.bnd.osgi.Analyzer
import aQute.bnd.osgi.Jar

import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class BndWrapper {


    static void wrapNonBundle( File jarFile, String bundlesDir) {
        println "Wrapping : ${jarFile.name}"

        def newJar = new Jar( jarFile )
        def currentManifest = newJar.manifest ?: new Manifest()


        String implVersion =currentManifest.mainAttributes.getValue( 'Implementation-Version' ) ?:
                        versionFromFileName( jarFile.name )

        String implTitle = currentManifest.mainAttributes.getValue( 'Implementation-Title' ) ?:
                        titleFromFileName( jarFile.name )

        String imports =  '*'
        String exports =  '*'

        def analyzer = new Analyzer().with {
            jar = newJar
            bundleVersion = implVersion
            bundleSymbolicName = implTitle
            importPackage = imports
            exportPackage = exports
            return it
        }

        Manifest manifest = analyzer.calcManifest()


        def bundle = new File( "$bundlesDir/${jarFile.name}" )
        boolean manifestDone = false
        BundleUtils.copyJar( jarFile, bundle ) {
            ZipFile input, ZipOutputStream out, ZipEntry entry ->
                if ( entry.name == 'META-INF/MANIFEST.MF' ) {
                    out.putNextEntry( new ZipEntry( entry.name ) )
                    manifest.write( out )
                    manifestDone = true
                } else {
                    out.putNextEntry( entry )
                    out.write( input.getInputStream( entry ).bytes )
                }
                if(!manifestDone && !BundleUtils.hasManifest(jarFile)){
                    out.putNextEntry( new ZipEntry( 'META-INF/MANIFEST.MF' ) )
                    manifest.write( out )
                    manifestDone = true
                }

        }

    }


    static String removeExtensionFrom( String name ) {
        def dot = name.lastIndexOf( '.' )
        if ( dot > 0 ) { // exclude extension
            return name[ 0..<dot ]
        }
        return name
    }

    static String versionFromFileName( String name ) {
        name = removeExtensionFrom( name )
        def digitsAfterDash = name.find( /\-\d+.*/ )
        if ( digitsAfterDash ) {
            return digitsAfterDash[ 1..-1 ] // without the dash
        }
        int digit = name.findIndexOf { it.number }
        if ( digit > 0 ) {
            return name[ digit..-1 ]
        }
        '1.0.0'
    }

    static String titleFromFileName( String name ) {
        name = removeExtensionFrom( name )
        def digitsAfterDash = name.find( /\-\d+.*/ )
        if ( digitsAfterDash ) {
            return name - digitsAfterDash
        }
        int digit = name.findIndexOf { it.number }
        if ( digit > 0 ) {
            return name[ 0..<digit ]
        }
        name
    }


}
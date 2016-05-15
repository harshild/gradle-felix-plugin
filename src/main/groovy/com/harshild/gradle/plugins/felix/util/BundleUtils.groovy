package com.harshild.gradle.plugins.felix.util

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Created by Harshil on 10-Apr-16.
 */
class BundleUtils {

    static void copyJar(File source, File destination,
                        Closure copyFunction,
                        Closure afterFunction = { _ -> } ) {
        def destinationStream = new ZipOutputStream( destination.newOutputStream() )
        def input = new ZipFile( source )
        try {
            for ( entry in input.entries() ) {
                copyFunction( input, destinationStream, entry )
            }
            afterFunction( destinationStream )
        } finally {
            try {
                destinationStream.close()
            } catch ( ignored ) {
            }
            try {
                input.close()
            } catch ( ignored ) {
            }
        }
    }

    static void fatJar(List sources, File destination,
                       Closure copyFunction,
                       Closure afterFunction = { _ -> } ) {
        def destinationStream = new ZipOutputStream( destination.newOutputStream() )
        for(source in sources) {
            def input = new ZipFile(source as String)
            try {
                for (entry in input.entries()) {
                    copyFunction(input, destinationStream, entry)
                }
                afterFunction(destinationStream)
            } finally {

                try {
                    input.close()
                } catch (ignored) {
                }
            }
        }
        try {
            destinationStream.close()
        } catch (ignored) {
        }
    }

    static withManifestEntry( file, Closure consumeManifest, Callable manifestMissing = { -> } ) {
        withJarEntry( file, 'META-INF/MANIFEST.MF', consumeManifest, manifestMissing )
    }


    static withJarEntry( file, String entryName,
                         Closure consumeEntry,
                         Callable entryMissing ) {
        def zip = new ZipFile( file as File )
        try {
            ZipEntry entry = zip.getEntry( entryName )
            if ( !entry ) return entryMissing()
            else return consumeEntry( zip, entry )
        } finally {
            zip.close()
        }
    }


    static boolean hasManifest( File file ) {
        withManifestEntry( file, { ZipFile zip, ZipEntry entry -> true }, { false } )
    }

    static boolean notBundle( File file ) {
        withManifestEntry( file, { ZipFile zip, ZipEntry entry ->
            def lines = zip.getInputStream( entry ).readLines()
            !lines.any { it.trim().startsWith( 'Bundle' ) }
        }, { true } )
    }

}

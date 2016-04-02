package com.harshil.gradle.plugins.felix.util

import java.util.concurrent.atomic.AtomicBoolean
/**
 * Created by Harshil on 02-Apr-16.
 */
class JarRunner {

    void run(String runDir ) {
            def java = javaCmd()

            def process = "$java -jar ${runDir}\\bin\\felix.jar "
                    .execute(null, new File("${runDir}"))

            delegateProcessTo( process )
    }

    private void delegateProcessTo( Process process ) {
        def exit = new AtomicBoolean( false )
        def line = null;

        consume process.in, exit, System.out
        consume process.err, exit, System.err

        def input = System.in.newReader()

        def readAtLeastOneLine = false

        while ( !exit.get() && ( line = input.readLine()?.trim() ) != null ) {
            if ( line in [ 'exit', 'stop 0', 'shutdown', 'quit' ] ) {
                exit.set true
                line = 'stop 0'
            }
            readAtLeastOneLine = true

            process.outputStream.write( ( line + '\n' ).bytes )
            process.outputStream.flush()
        }

        if ( readAtLeastOneLine ) {
            try {
                process.waitForOrKill( 5000 )
            } catch ( e ) {
                println(e.printStackTrace())
            } finally {
                exit.set true
            }
        } else {
            def stars = '*' * 50
            println "$stars\n" +
                    "The felix-plugin process does not have access to the\n" +
                    "JVM console (may happen when running from an IDE).\n" +
                    "For this reason, the command-line will not work.\n" +
                    "$stars"

            process.waitFor()
            exit.set true
        }
    }

    void consume( InputStream stream, AtomicBoolean exit, PrintStream writer ) {
        Thread.startDaemon {
            byte[] bytes = new byte[64]
            while ( !exit.get() ) {
                def len = stream.read( bytes )
                if ( len > 0 ) writer.write bytes, 0, len
                else exit.set( true )
            }
        }
    }

    static String javaCmd() {
        def javaHome = System.getenv( 'JAVA_HOME' )
        if ( javaHome ) {
            def potentialJavas = [ "$javaHome/bin/java", "$javaHome/jre/bin/java" ]
                    .collect { it.replace( '//', '/' ).replace( '\\\\', '/' ) }
            for ( potentialJava in potentialJavas ) {
                if ( new File( potentialJava ).exists() ) {
                    return potentialJava
                }
            }
        }
        return 'java'
    }
}

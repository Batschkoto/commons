package de.batschkoto.commons.logger;

import de.batschkoto.commons.util.ChatColor;
import jline.console.ConsoleReader;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CommonsLogger extends Logger {

    private final LogDispatcher dispatcher = new LogDispatcher( this );

    public CommonsLogger( ConsoleReader consoleReader, String logName, ChatColor chatColor ) {
        super( "Commons", null );
        setLevel( Level.ALL );

        try {
            File logDir = new File( "logs" );
            if ( !logDir.exists() ) {
                if ( !logDir.mkdir() ) {
                    throw new RuntimeException( "Could not create log dir. Please be sure to check the Filesystem permissions" );
                }
            }

            FileHandler fileHandler = new FileHandler( "logs" + File.separator + logName + ".log", 1 << 24, 8, true );
            fileHandler.setFormatter( new ConciseFormatter( false, null ) );
            addHandler( fileHandler );

            ColouredWriter consoleHandler = new ColouredWriter( consoleReader );
            consoleHandler.setLevel( Level.INFO );
            consoleHandler.setFormatter( new ConciseFormatter( true, chatColor ) );
            addHandler( consoleHandler );
        } catch ( IOException ex ) {
            System.err.println( "Could not register logger!" );
            ex.printStackTrace();
        }

        dispatcher.start();
    }

    @Override
    public void log( LogRecord record ) {
        dispatcher.queue( record );
    }

    void doLog( LogRecord record ) {
        super.log( record );
    }
}
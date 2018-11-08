package de.batschkoto.commons.logger;

import de.batschkoto.commons.Commons;
import de.batschkoto.commons.commands.CommandManager;
import de.batschkoto.commons.util.ChatColor;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;

/**
 * @author batschkoto
 */
public class LoggingManager {

    private final Commons commons;
    private final ChatColor chatColor;

    @Getter
    private ConsoleReader consoleReader;
    @Getter
    private CommonsLogger logger;

    public LoggingManager( Commons commons, ChatColor chatColor ) {
        this.commons = commons;
        this.chatColor = chatColor;

        init();
    }

    private void init() {
        try {
            consoleReader = new ConsoleReader();
            consoleReader.setExpandEvents( false );

            logger = new CommonsLogger( consoleReader, commons.getApplicationName().toLowerCase(), chatColor );
            System.setErr( new PrintStream( new LoggingOutputStream( logger, Level.SEVERE ), true ) );
            System.setOut( new PrintStream( new LoggingOutputStream( logger, Level.INFO ), true ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook( new Thread( "JLine Cleanup Thread" ) {
            @Override
            public void run() {
                try {
                    consoleReader.getTerminal().restore();
                } catch ( Exception ex ) {
                    // Ignored
                }
            }
        } );

        // Author and Version infos
        logger.info( "======================================================" );
        logger.info( "===" );
        logger.info( "===     " + commons.getApplicationName() + " " + commons.getVersion() );
        logger.info( "===     Developed by " + commons.getAuthor() );
        logger.info( "===" );
        logger.info( "======================================================" );
    }

    public void readCommands( CommandManager commandManager ) {
        try {
            while ( true ) {
                String line = consoleReader.readLine( "> " );
                if ( line != null ) {
                    if ( !commandManager.executeCommand( line ) ) {
                        logger.warning( ChatColor.RED + "Unknown command!" );
                    }
                }
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
}

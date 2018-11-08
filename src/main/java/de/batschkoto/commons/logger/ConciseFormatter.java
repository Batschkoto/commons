package de.batschkoto.commons.logger;

import de.batschkoto.commons.util.ChatColor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ConciseFormatter extends Formatter {

    private final DateFormat date = new SimpleDateFormat( "HH:mm:ss" );
    private final boolean colored;
    private final ChatColor chatColor;

    public ConciseFormatter( boolean colored, ChatColor chatColor ) {
        this.colored = colored;
        this.chatColor = chatColor;
    }

    @Override
    public String format( LogRecord record ) {
        StringBuilder formatted = new StringBuilder();

        if ( colored ) {
            if ( record.getLevel().equals( Level.WARNING ) ) {
                formatted.append( ChatColor.RED );
            } else if ( record.getLevel().equals( Level.SEVERE ) ) {
                formatted.append( ChatColor.DARK_RED );
            } else {
                formatted.append( chatColor );
            }
        }

        formatted.append( "[" );
        formatted.append( date.format( record.getMillis() ) );
        formatted.append( " - " );
        formatted.append( record.getLevel().getLocalizedName() );
        formatted.append( "] " );

        if ( colored ) {
            formatted.append( ChatColor.RESET );
        }

        formatted.append( formatMessage( record ) );
        formatted.append( '\n' );

        if ( record.getThrown() != null ) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace( new PrintWriter( writer ) );
            formatted.append( writer );
        }

        return formatted.toString();
    }
}
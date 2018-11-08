package de.batschkoto.commons.commands;

import com.google.common.reflect.ClassPath;
import de.batschkoto.commons.Commons;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class CommandManager {

    private final Logger logger;

    public CommandManager( Logger logger ) {
        this.logger = logger;
    }

    private final Map<String, AbstractCommand> commandMap = Collections.synchronizedMap( new HashMap<>() );

    private void registerCommand( AbstractCommand command ) {
        commandMap.put( command.getCommand().toLowerCase(), command );
    }

    public void registerCommands( String packagePath ) {
        try {
            for ( ClassPath.ClassInfo classInfo : ClassPath.from( Commons.class.getClassLoader() ).getTopLevelClassesRecursive( packagePath ) ) {
                Class cls = Class.forName( classInfo.getName(), true, Commons.class.getClassLoader() );

                if ( AbstractCommand.class.equals( cls.getSuperclass() ) ) {
                    AbstractCommand command = (AbstractCommand) cls.newInstance();
                    registerCommand( command );

                    if ( logger != null ) {
                        logger.info( "Registered command " + command.getCommand() );
                    }
                }
            }
        } catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e ) {
            e.printStackTrace();
        }
    }

    public boolean executeCommand( String commandLine ) {
        String command = commandLine.split( " " )[0];
        String[] args = new String[0];

        if ( commandLine.length() > command.length() ) {
            args = commandLine.substring( command.length() + 1 ).split( " " );
        }

        if ( commandMap.containsKey( command.toLowerCase() ) ) {
            commandMap.get( command ).execute( args );
            return true;
        }

        return false;
    }
}

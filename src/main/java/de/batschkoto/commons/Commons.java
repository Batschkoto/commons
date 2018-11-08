package de.batschkoto.commons;

import de.batschkoto.commons.async.TaskManager;
import de.batschkoto.commons.commands.CommandManager;
import de.batschkoto.commons.logger.LoggingManager;
import de.batschkoto.commons.mysql.MySQL;
import de.batschkoto.commons.util.ChatColor;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author batschkoto
 */
@Getter
public class Commons {

    private final String applicationName;
    private final String author;
    private final String version;
    private final ChatColor chatColor;

    private final CommandManager commandManager;
    private final LoggingManager loggingManager;

    private final Logger logger;
    private final ExecutorService executorService;
    private MySQL mysql;

    public Commons( String applicationName, String author, String version, ChatColor chatColor ) {
        this( applicationName, author, version, chatColor, null );
    }

    public Commons( String applicationName, String author, String version, ChatColor chatColor, ExecutorService executorService ) {
        // General
        this.applicationName = applicationName;
        this.author = author;
        this.version = version;
        this.chatColor = chatColor;

        // Logger
        this.loggingManager = new LoggingManager( this, chatColor );
        this.logger = loggingManager.getLogger();

        // Command Manager
        this.commandManager = new CommandManager( logger );

        // Executor Service
        this.executorService = executorService == null ? Executors.newCachedThreadPool() : executorService;

        // TaskManager
        TaskManager.init( this.executorService );
    }

    public boolean connectMysql( String host, String username, String password, String database, String url, int poolSize ) {
        mysql = new MySQL( logger, host, username, password, database, url, poolSize );
        mysql.connect();

        return mysql.isConnected();
    }

    public void readCommands() {
        loggingManager.readCommands( commandManager );
    }

}

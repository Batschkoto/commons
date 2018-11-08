package de.batschkoto.commons.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class MySQL {

    private HikariDataSource dataSource = new HikariDataSource();

    private final Logger logger;
    private final String host;
    private final String username;
    private final String password;
    private final String database;
    private final String url;
    private final int poolSize;

    @Getter
    private boolean connected;

    public MySQL( Logger logger, String host, String username, String password, String database ) {
        this( logger, host, username, password, database, "", 1 );
    }

    public MySQL( Logger logger, String host, String username, String password, String database, String url ) {
        this( logger, host, username, password, database, url, 1 );
    }

    public MySQL( Logger logger, String host, String username, String password, String database, String url, int poolSize ) {
        this.logger = logger;
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.poolSize = poolSize;
        this.url = url == null ? "" : "?" + url;

        setUp();
    }

    private void setUp() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( String.format( "jdbc:mysql://%s:3306/%s%s", host, database, url ) );
        config.setUsername( username );
        config.setPassword( password );

        config.setMaximumPoolSize( poolSize );
        config.setPoolName( "MYSQL-POOL" );
        config.setConnectionTestQuery( "SELECT 1" );

        config.addDataSourceProperty( "dataSource.cachePrepStmts", "true" );
        config.addDataSourceProperty( "dataSource.prepStmtCacheSize", "250" );
        config.addDataSourceProperty( "dataSource.prepStmtCacheSqlLimit", "2048" );
        config.addDataSourceProperty( "dataSource.useServerPrepStmts", "true" );

        dataSource = new HikariDataSource( config );
    }

    public void connect() {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();

            connected = true;
        } catch ( SQLException e ) {
            logger.warning( "Error while checking if the Connection is correct" );
            e.printStackTrace();
        }
    }

    public void close() {
        dataSource.close();
        connected = false;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    public void giveBack( Connection connection ) {
        try {
            connection.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public void update( String query, Object... objects ) {
        Connection connection = getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement( query );
            applyParameters( statement, objects );

            statement.executeUpdate();
            statement.close();
        } catch ( SQLException sqlex ) {
            logger.warning( "Could not execute: " + query );
            sqlex.printStackTrace();
        } finally {
            giveBack( connection );
        }
    }

    public List<Integer> insert( String query, Object... objects ) {
        Connection connection = getConnection();
        List<Integer> idList = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement( query, Statement.RETURN_GENERATED_KEYS );
            applyParameters( statement, objects );

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();

            while ( rs.next() ) {
                idList.add( rs.getInt( 1 ) );
            }

            rs.close();
            statement.close();
        } catch ( SQLException sqlex ) {
            logger.warning( "Could not execute: " + query );
            sqlex.printStackTrace();
        } finally {
            giveBack( connection );
        }

        return idList;
    }

    public DBResult query( String query, Object... objects ) {
        Connection connection = getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement( query );
            applyParameters( statement, objects );

            if ( statement.execute() ) {
                DBResult dbResult = new DBResult();

                ResultSet set = statement.getResultSet();
                ResultSetMetaData resultSetMetaData = set.getMetaData();
                int amountColumns = resultSetMetaData.getColumnCount();
                List<String> columnNames = new ArrayList<>();

                for ( int i = 1; i < amountColumns + 1; i++ ) {
                    columnNames.add( resultSetMetaData.getColumnName( i ) );
                }

                while ( set.next() ) {
                    DBResult.DBRow dbRow = new DBResult.DBRow();
                    columnNames.forEach( name -> {
                        try {
                            dbRow.addData( name, set.getObject( name ) );
                        } catch ( SQLException e ) {
                            e.printStackTrace();
                        }
                    } );
                    dbResult.addRow( dbRow );
                }

                set.close();
                statement.close();

                return dbResult;
            }
        } catch ( SQLException sqlex ) {
            logger.warning( "Could not execute: " + query );
            sqlex.printStackTrace();
        } finally {
            giveBack( connection );
        }

        return null;
    }

    private void applyParameters( PreparedStatement statement, Object[] objects ) throws SQLException {
        for ( int i = 0; i < objects.length; i++ ) {
            if ( objects[i] instanceof String ) {
                statement.setString( i + 1, (String) objects[i] );
            } else if ( objects[i] instanceof Integer ) {
                statement.setInt( i + 1, (Integer) objects[i] );
            } else if ( objects[i] instanceof Long ) {
                statement.setLong( i + 1, (Long) objects[i] );
            } else if ( objects[i] instanceof Double ) {
                statement.setDouble( i + 1, (Double) objects[i] );
            } else if ( objects[i] instanceof Short ) {
                statement.setShort( i + 1, (Short) objects[i] );
            } else if ( objects[i] instanceof Float ) {
                statement.setFloat( i + 1, (Float) objects[i] );
            } else if ( objects[i] instanceof Boolean ) {
                statement.setBoolean( i + 1, (Boolean) objects[i] );
            } else if ( objects[i] instanceof Timestamp ) {
                statement.setTimestamp( i + 1, (Timestamp) objects[i] );
            } else if ( objects[i] instanceof UUID ) {
                statement.setString( i + 1, ( (UUID) objects[i] ).toString() );
            }
        }
    }
}
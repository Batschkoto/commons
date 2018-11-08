package de.batschkoto.commons.mysql;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.*;

public class DBResult {
    public static class DBRow {
        @Getter
        private Map<String, Object> content = new HashMap<>();

        public void addData( String key, Object object ) {
            content.put( key, object );
        }

        public int getInt( String key ) {
            Preconditions.checkArgument( content.containsKey( key ), "Key was not found" );

            Object obj = content.get( key );
            if ( obj instanceof Double ) {
                return getDouble( key ).intValue();
            }

            if ( obj instanceof Long ) {
                return getLong( key ).intValue();
            }

            return (int) content.get( key );
        }

        public String getString( String key ) {
            Preconditions.checkArgument( content.containsKey( key ), "Key was not found" );
            return (String) content.get( key );
        }

        public Double getDouble( String key ) {
            Preconditions.checkArgument( content.containsKey( key ), "Key was not found" );
            return (Double) content.get( key );
        }

        public Long getLong( String key ) {
            return (Long) content.get( key );
        }

        public Boolean getBoolean( String key ) {
            return (Boolean) content.get( key );
        }

        public Timestamp getTimestamp( String key ) {
            return (Timestamp) content.get( key );
        }

        public UUID getUuid( String key ) {
            return UUID.fromString( getString( key ) );
        }

        public void rawSetMap( Map<String, Object> map ) {
            this.content = map;
        }
    }

    @Getter
    private List<DBRow> rows = new ArrayList<>();

    public void addRow( DBRow dbRow ) {
        rows.add( dbRow );
    }
}

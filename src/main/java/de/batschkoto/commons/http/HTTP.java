package de.batschkoto.commons.http;

import de.batschkoto.commons.async.Callback;
import de.batschkoto.commons.async.TaskManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author batschkoto
 */
public class HTTP {

    private String url;
    private Proxy proxy;
    private int timeout = 10000;
    private String method = "GET";
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();

    public enum Method {
        POST, GET;
    }

    public HTTP setUrl( String url ) {
        this.url = url;
        return this;
    }

    public HTTP setMethod( Method method ) {
        this.method = method.equals( Method.POST ) ? "POST" : "GET";
        return this;
    }

    public HTTP setProxy( Proxy proxy ) {
        this.proxy = proxy;
        return this;
    }

    public HTTP setTimeout( int timeout ) {
        this.timeout = timeout;
        return this;
    }

    public HTTP addHeader( String key, String value ) {
        this.headers.put( key, value );
        return this;
    }

    public HTTP setHeaders( Map<String, String> headers ) {
        this.headers.clear();
        this.headers.putAll( headers );
        return this;
    }

    public HTTP addParameter( String key, String value ) {
        this.parameters.put( key, value );
        return this;
    }

    public HTTP setParameters( Map<String, String> parameters ) {
        this.parameters.clear();
        this.parameters.putAll( parameters );
        return this;
    }

    private HttpURLConnection getHttpConnection() throws IOException {
        if ( url.toLowerCase().startsWith( "https://" ) ) {
            if ( proxy == null ) {
                return (HttpsURLConnection) new URL( url ).openConnection();
            } else {
                return (HttpsURLConnection) new URL( url ).openConnection( proxy );
            }
        } else {
            if ( proxy == null ) {
                return (HttpURLConnection) new URL( url ).openConnection();
            } else {
                return (HttpURLConnection) new URL( url ).openConnection( proxy );
            }
        }
    }

    public String perform() {
        try {
            HttpURLConnection con = getHttpConnection();

            // reuqest header
            con.setRequestMethod( method );

            con.setReadTimeout( timeout );
            con.setConnectTimeout( timeout );

            headers.entrySet().forEach( entry -> con.setRequestProperty( entry.getKey(), entry.getValue() ) );

            if ( method.equalsIgnoreCase( "POST" ) ) {
                StringBuilder urlParameters = new StringBuilder();

                parameters.entrySet().forEach( entry -> urlParameters.append( entry.getKey() ).append( "=" ).append( entry.getValue() ).append( "&" ) );
                if ( urlParameters.length() > 0 ) {
                    urlParameters.setLength( urlParameters.length() - 1 );
                }

                // Send post
                con.setDoOutput( true );
                DataOutputStream wr = new DataOutputStream( con.getOutputStream() );
                wr.writeBytes( urlParameters.toString() );
                wr.flush();
                wr.close();
            }

            BufferedReader reader = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
            StringBuilder result = new StringBuilder();
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                result.append( line );
            }

            return result.toString();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    public void performAsync( Callback<String> callback ) {
        TaskManager.execute( () -> callback.done( perform() ) );
    }

}

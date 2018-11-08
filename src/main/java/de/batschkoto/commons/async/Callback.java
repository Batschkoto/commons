package de.batschkoto.commons.async;

public interface Callback<T> {
    void done( T arg );
}

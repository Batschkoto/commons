package de.batschkoto.commons.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author batschkoto
 */
public class TaskManager {

    private static ExecutorService executorService;

    public static void init( ExecutorService executorService1 ) {
        executorService = executorService1;
    }

    public static void execute( Runnable runnable ) {
        executorService.execute( runnable );
    }

    public static void schedule( Runnable runnable, int delay ) {
        executorService.execute( () -> {
            try {
                Thread.sleep( delay );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

            runnable.run();
        } );
    }

    public static void repeat( Runnable runnable, int delay, int period, AtomicBoolean condition ) {
        executorService.execute( () -> {
            try {
                Thread.sleep( delay );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

            while ( condition.get() ) {
                runnable.run();

                try {
                    Thread.sleep( period );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }
}

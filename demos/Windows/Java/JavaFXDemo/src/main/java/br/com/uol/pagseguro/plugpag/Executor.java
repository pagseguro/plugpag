package br.com.uol.pagseguro.plugpag;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Manager of a thread pool to execute asynchronous tasks.
 */
public class Executor {

    // -----------------------------------------------------------------------------------------------------------------
    // Class attributes
    // -----------------------------------------------------------------------------------------------------------------

    public static ExecutorService sService = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Task scheduling
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Submits a new task to be run.
     *
     * @param task Task to be submitted.
     */
    public static final void submit(Runnable task) {
        if (Executor.sService == null) {
            Executor.sService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }

        Executor.sService.submit(task);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Resource release
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Releases the Executor reference.
     */
    public static final void release() {
        if (Executor.sService != null) {
            Executor.sService.shutdownNow();
        }

        Executor.sService = null;
    }

}

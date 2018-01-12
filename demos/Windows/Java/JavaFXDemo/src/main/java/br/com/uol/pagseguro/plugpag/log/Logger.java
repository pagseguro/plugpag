package br.com.uol.pagseguro.plugpag.log;

import br.com.uol.pagseguro.plugpag.log.writer.ConsoleWriter;
import br.com.uol.pagseguro.plugpag.log.writer.LogWriter;
import br.com.uol.pagseguro.plugpag.log.writer.NullWriter;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Logging utility class.
 */
public final class Logger {

    // -----------------------------------------------------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------------------------------------------------

    private static final String TAG = "PagSeguro/PlugPag";
    private static final DateTimeFormatter DATETIMEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LogWriter LOG_WRITER = new ConsoleWriter();

    // -----------------------------------------------------------------------------------------------------------------
    // Class attributes
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Singleton instance.
     */
    private static Logger sInstance = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Log handling
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes a new log entry.
     *
     * @param message Message to be logged.
     */
    public static final void log(String message) {
        Logger.getInstance().logMessage(message);
    }

    /**
     * Writes a new log entry.
     *
     * @param message   Message to be logged.
     * @param throwable Throwable to be logged.
     */
    public static final void log(String message, Throwable throwable) {
        Logger.getInstance().logMessage(message, throwable);
    }

    /**
     * Writes a new log entry.
     *
     * @param args Arguments of the message to be logged.
     */
    public static void log(Object... args) {
        Logger.getInstance().logMessage(args);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Instance manager
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a logger instance.
     *
     * @return Logger instance.
     */
    private static final Logger getInstance() {
        if (Logger.sInstance == null) {
            Logger.sInstance = new Logger(Logger.LOG_WRITER);
        }

        return Logger.sInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * LogWriter used to handle a log message.
     */
    private LogWriter mWriter = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new Logger instance.
     *
     * @param writer Writer used to handle a log message.
     */
    private Logger(LogWriter writer) {
        if (writer == null) {
            throw new InvalidParameterException("LogWriter reference cannot be null");
        }

        this.mWriter = writer;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Log handlers
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes a new log entry.
     *
     * @param message Message to be logged.
     */
    private void logMessage(String message) {
        if (message != null) {
            this.mWriter.write(String.format("[%s] [%s] %s", this.getTimestamp(), Logger.TAG, message));
        } else {
            this.mWriter.write(String.format("[%s] [%s]", this.getTimestamp(), Logger.TAG));
        }
    }

    /**
     * Writes a new log entry.
     *
     * @param args Arguments of the message to be logged.
     */
    private void logMessage(Object... args) {
        String message = null;

        if (args != null && args.length > 1) {
            message = String.format((String) args[0], Arrays.copyOfRange(args, 1, args.length));
        }

        this.logMessage(message);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Date and time manipulation
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a default format of the current date and time, considering current locale.
     *
     * @return Formatted date and time.
     */
    private String getTimestamp() {
        return LocalDateTime.now().format(Logger.DATETIMEFORMATTER);
    }

}

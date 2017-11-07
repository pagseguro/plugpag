package br.com.uol.pagseguro.plugpag.log.writer;

/**
 * LogWriter to output messages to the console.
 */
public class ConsoleWriter implements LogWriter {

    // -----------------------------------------------------------------------------------------------------------------
    // Log writing
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void write(String message) {
        if (message != null) {
            System.out.println(message);
        }
    }

}

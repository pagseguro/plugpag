package br.com.uol.pagseguro.plugpag.log.writer;

/**
 * LogWriter to discard any message.
 */
public class NullWriter implements LogWriter {

    @Override
    public void write(String message) {
        // Ignore the message
    }

}

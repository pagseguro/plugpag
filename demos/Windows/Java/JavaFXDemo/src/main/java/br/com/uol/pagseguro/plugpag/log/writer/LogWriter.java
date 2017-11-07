package br.com.uol.pagseguro.plugpag.log.writer;

public interface LogWriter {

    /**
     * Writes a message to some output.
     *
     * @param message Message to be written.
     */
    void write(String message);

}

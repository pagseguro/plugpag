package br.com.uol.pagseguro.plugpagandroiddemo;


public interface TaskHandler {

    void onTaskStart();

    void onProgressPublished(String progress, Object transactionInfo);

    void onTaskFinished(Object result);

}

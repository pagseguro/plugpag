package br.uol.pagseguro.client.pagcafe.model;

/**
 * Created by tqi_hsantos on 11/07/17.
 */

public class Transaction {

    private Type_Transaction type;
    private String message;
    private String transactionCode;
    private String date;
    private String time;
    private String hostNsu;
    private String cardBrand;
    private String bin;
    private String holder;
    private String userReference;
    private String terminalSerialNumber;

    private int result;

    private int paymentMethod;
    private String samount;

    public Transaction(Type_Transaction type) {
        this.type = type;
    }

    public Transaction(Type_Transaction type, int paymentMethod, String samount) {
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.samount = samount;
    }

    public Type_Transaction getType() {

        return type;
    }

    public void setType(Type_Transaction type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHostNsu() {
        return hostNsu;
    }

    public void setHostNsu(String hostNsu) {
        this.hostNsu = hostNsu;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getUserReference() {
        return userReference;
    }

    public void setUserReference(String userReference) {
        this.userReference = userReference;
    }

    public String getTerminalSerialNumber() {
        return terminalSerialNumber;
    }

    public void setTerminalSerialNumber(String terminalSerialNumber) {
        this.terminalSerialNumber = terminalSerialNumber;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSamount() {
        return samount;
    }

    public void setSamount(String samount) {
        this.samount = samount;
    }
}

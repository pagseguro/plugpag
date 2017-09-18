package br.uol.pagseguro.client.pagcafe.model;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;

public class Transaction implements Serializable {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Type_Transaction mType = null;
    private String mMessage = null;
    private String mTransactionCode = null;
    private String mDate = null;
    private String mTime = null;
    private String mHostNsu = null;
    private String mCardBrand = null;
    private String mBin = null;
    private String mHolder = null;
    private String mUserReference = null;
    private String mTerminalSerialNumber = null;
    private int mPaymentMethod = 0;
    private int mResult = 0;
    private String mAmount = null;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new empty Transaction.
     *
     * @param type Transaction type.
     */
    public Transaction(@NonNull Type_Transaction type) {
        if (type == null) {
            throw new RuntimeException("Transaction type cannot be null");
        }

        this.mType = type;
    }

    /**
     * Creates a new Transaction.
     *
     * @param type Transaction type.
     * @param paymentMethod Payment method used in the transaction.
     * @param amount String representation of the transaction value.
     */
    public Transaction(@NonNull Type_Transaction type, int paymentMethod, @NonNull String amount) {
        if (type == null) {
            throw new RuntimeException("Transaction type cannot be null");
        }

        if (TextUtils.isEmpty(amount)) {
            throw new RuntimeException("Transaction amount cannot be null");
        }

        this.mType = type;
        this.mPaymentMethod = paymentMethod;
        this.mAmount = amount;
    }

    /**
     * Creates a new Transaction copying the contents of another Transaction.
     *
     * @param otherTransaction Transaction to be copied.
     */
    public Transaction(@Nullable Transaction otherTransaction) {
        this.mType = otherTransaction.mType;
        this.mMessage = otherTransaction.mMessage;
        this.mTransactionCode = otherTransaction.mTransactionCode;
        this.mDate = otherTransaction.mDate;
        this.mTime = otherTransaction.mTime;
        this.mHostNsu = otherTransaction.mHostNsu;
        this.mCardBrand = otherTransaction.mCardBrand;
        this.mBin = otherTransaction.mBin;
        this.mHolder = otherTransaction.mHolder;
        this.mUserReference = otherTransaction.mUserReference;
        this.mTerminalSerialNumber = otherTransaction.mTerminalSerialNumber;
        this.mResult = otherTransaction.mResult;
        this.mPaymentMethod = otherTransaction.mPaymentMethod;
        this.mAmount = otherTransaction.mAmount;
    }

    // ---------------------------------------------------------------------------------------------
    // Getters and setters
    // ---------------------------------------------------------------------------------------------

    public Type_Transaction getType() {
        return mType;
    }

    public void setType(Type_Transaction type) {
        this.mType = type;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getTransactionCode() {
        return mTransactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.mTransactionCode = transactionCode;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String getHostNsu() {
        return mHostNsu;
    }

    public void setHostNsu(String hostNsu) {
        this.mHostNsu = hostNsu;
    }

    public String getCardBrand() {
        return mCardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.mCardBrand = cardBrand;
    }

    public String getBin() {
        return mBin;
    }

    public void setBin(String bin) {
        this.mBin = bin;
    }

    public String getHolder() {
        return mHolder;
    }

    public void setHolder(String holder) {
        this.mHolder = holder;
    }

    public String getUserReference() {
        return mUserReference;
    }

    public void setUserReference(String userReference) {
        this.mUserReference = userReference;
    }

    public String getTerminalSerialNumber() {
        return mTerminalSerialNumber;
    }

    public void setTerminalSerialNumber(String terminalSerialNumber) {
        this.mTerminalSerialNumber = terminalSerialNumber;
    }

    public int getResult() {
        return mResult;
    }

    public void setResult(int result) {
        this.mResult = result;
    }

    public int getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        this.mAmount = amount;
    }
}

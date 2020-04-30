package controller;

import javafx.beans.property.*;

public class TransactionTableDataModel {
    private final StringProperty date;
    private final IntegerProperty referenceNumber;
    private final StringProperty transactionType;
    private final DoubleProperty amount;

    public TransactionTableDataModel(String date, Integer referenceNumber, String transactionType, Double amount) {
        this.date = new SimpleStringProperty(date);
        this.referenceNumber = new SimpleIntegerProperty(referenceNumber);
        this.transactionType = new SimpleStringProperty(transactionType);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public int getReferenceNumber() {
        return referenceNumber.get();
    }

    public IntegerProperty referenceNumberProperty() {
        return referenceNumber;
    }

    public void setReferenceNumber(int referenceNumber) {
        this.referenceNumber.set(referenceNumber);
    }

    public String getTransactionType() {
        return transactionType.get();
    }

    public StringProperty transactionTypeProperty() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType.set(transactionType);
    }

    public double getAmount() {
        return amount.get();
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }
}

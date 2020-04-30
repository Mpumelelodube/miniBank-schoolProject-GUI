package controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

public class Bank {
    private final IntegerProperty accountNumber;
    private final IntegerProperty pin;

    public Bank(Integer accountNumber, Integer pin) {
        this.accountNumber = new SimpleIntegerProperty(accountNumber);
        this.pin = new SimpleIntegerProperty(pin);
    }

    public int getAccountNumber() {
        return accountNumber.get();
    }

    public IntegerProperty accountNumberProperty() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber.set(accountNumber);
    }

    public int getPin() {
        return pin.get();
    }

    public IntegerProperty pinProperty() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin.set(pin);
    }
}

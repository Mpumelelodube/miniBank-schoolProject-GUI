package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final StringProperty name;
    private final StringProperty lastName;
    private final StringProperty NationalID;
    private final StringProperty adress;
    private Bank bank;

    public User(String name, String lastName, String nationalID, String adress, Bank bank) {
        this.name = new SimpleStringProperty(name);
        this.lastName = new SimpleStringProperty(lastName);
        NationalID = new SimpleStringProperty(nationalID);
        this.adress = new SimpleStringProperty(adress);
        this.bank = bank;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getNationalID() {
        return NationalID.get();
    }

    public StringProperty nationalIDProperty() {
        return NationalID;
    }

    public void setNationalID(String nationalID) {
        this.NationalID.set(nationalID);
    }

    public String getAdress() {
        return adress.get();
    }

    public StringProperty adressProperty() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress.set(adress);
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}

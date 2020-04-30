package controller;

import databaseConnection.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.*;

public class Controller implements Initializable {

    ObservableList<String> emp = FXCollections.observableArrayList("Employed","Self Employed","Unemployed");
    ObservableList<String> month = FXCollections.observableArrayList("January","February","March","April","May",
            "JUne","July","August","September","October","November","December");

    @FXML
    private ComboBox months;

    @FXML
    private Label balanceLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        employment.setItems(emp);
        months.setItems(month);

        deactivateWithdrawField.setEditable(false);
        deactivateTransferAcc.setEditable(false);
        deactivateTransferAmt.setEditable(false);
        deTransferOk.setDisable(true);
        deWithdrawOk.setDisable(true);
        deTransferVerify.setDisable(true);
        deactivationConfirm.setDisable(true);
        transferRadio.setDisable(true);
        withdrawRadio.setDisable(true);
    }

    ////////////////////////////////////////////////LOGIN///////////////////////////////////////////////////////////////

    @FXML
    private PasswordField pinField;

    @FXML
    private TextField accountNumberField;

    @FXML
    private Label name;

    @FXML
    private Label lastName;

    @FXML
    private Label account;

    @FXML
    private Label id;

    private List<User> users = new ArrayList<>();

    public void login(){
        String sql = "select users.first_name,users.last_name,accounts.national_id,users.address,accounts.account,accounts.pin from accounts,users where account = ? and pin = ? and accounts.national_id == users.national_id";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,Integer.parseInt(accountNumberField.getText()));
            preparedStatement.setInt(2,Integer.parseInt(pinField.getText()));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                users.add(new User(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),new Bank(resultSet.getInt(5),resultSet.getInt(6))));
                System.out.println(resultSet.getString(1));
                this.paneDashboard.toFront();
                this.formPane.toFront();
            }

            name.setText(users.get(0).getName());
            lastName.setText(users.get(0).getLastName());
            id.setText(users.get(0).getNationalID());
            account.setText(String.valueOf(users.get(0).getBank().getAccountNumber()));
            balanceLabel.setText(String.valueOf(checkBalance()));

            pinField.clear();
            accountNumberField.clear();

        } catch (SQLException | NumberFormatException | IndexOutOfBoundsException | NullPointerException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("ERROR logging Please recheck your credentials");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////DEPOSIT MONEY//////////////////////////////////////////////////////////

    @FXML
    private TextField depositAmountTextField;

    @FXML
    private PasswordField depositPinVerification;

    @FXML
    private void depositMoney() {
        if (Integer.parseInt(depositPinVerification.getText()) == users.get(0).getBank().getPin()){
            String sql = "insert into transactions(date,amount)values(?,?)";

            try {
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1,getDate());
                preparedStatement.setDouble(2, Double.parseDouble(depositAmountTextField.getText()));

                preparedStatement.execute();

                connection.close();

                insertBalance(1,this.users.get(0).getBank().getAccountNumber());
                System.out.println("deposit successful new balance: $"+checkBalance());

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("You have successfully deposited $"+this.depositAmountTextField.getText()+"\nnew balance $"+checkBalance());
                alert.showAndWait();

                depositAmountTextField.clear();
                depositPinVerification.clear();
                balanceLabel.setText(String.valueOf(checkBalance()));

            } catch (SQLException | NumberFormatException | NullPointerException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("ERROR Please recheck your input data");
                alert.showAndWait();
                e.printStackTrace();
            }
        }else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("Incorrect pin \nrecheck your input data");
            alert.showAndWait();
        }
    }

    ///////////////////////////////////////////////WITHDRAW MONEY//////////////////////////////////////////////////////

    @FXML
    private TextField withdrawAmountTextField;

    @FXML
    private PasswordField withdrawPinVerification;

    @FXML
    private void withdrawMoney() {

        if (checkBalance() > Double.parseDouble(withdrawAmountTextField.getText())-50){
            if (Integer.parseInt(withdrawPinVerification.getText()) == users.get(0).getBank().getPin()){
                String sql = "insert into transactions(date,amount)values(?,?)";

                try {
                    Connection connection = DatabaseConnection.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setString(1,getDate());
                    preparedStatement.setDouble(2,Double.parseDouble(withdrawAmountTextField.getText()));

                    preparedStatement.execute();

                    connection.close();

                    insertBalance(2,this.users.get(0).getBank().getAccountNumber());

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("You have successfully withdrawn $"+this.withdrawAmountTextField.getText()+"\nnew balance $"+checkBalance());
                    alert.showAndWait();

                    balanceLabel.setText(String.valueOf(checkBalance()));
                    withdrawAmountTextField.clear();
                    withdrawPinVerification.clear();

                } catch (SQLException | NumberFormatException | NullPointerException e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("Please Fill in all Fields and \nrecheck your input data");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("Incorrect pin \nrecheck your input data");
                alert.showAndWait();
            }
        }else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("You have insufficient funds to\nprocess this transaction");
            alert.showAndWait();
        }

       /* if (!withdrawAmountTextField.getText().equals(null) && !withdrawPinVerification.getText().equals(null)){

        }else {

        }*/
    }

    /////////////////////////////////////////////TRANSFER MONEY/////////////////////////////////////////////////////////

    @FXML
    private Label transferName;

    @FXML
    private Label transferLastName;

    @FXML
    private Label transferAccount;

    @FXML
    private Button transferConfirmationButton;

    @FXML
    private TextField transferAmountTextField;

    @FXML
    private TextField transferDestinationAccount;

    @FXML
    private PasswordField transferPinVerification;

    @FXML
    private void transferMoney() {

        String sql = "select national_id from accounts where account = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, Integer.parseInt(this.transferDestinationAccount.getText()));

            ResultSet resultSet = preparedStatement.executeQuery();

            String id = resultSet.getString(1);

            if (!id.equals(null)){
                sql = "select users.first_name,users.last_name,accounts.account from users,accounts where accounts.national_id = ? and accounts.national_id == users.national_id";

                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, id);

                resultSet = preparedStatement.executeQuery();

                System.out.println("Transfering money to "+resultSet.getString(1)+" "+resultSet.getString(2)+" Account number: "+account);

                this.transferAccount.setText(resultSet.getString(3));
                this.transferLastName.setText(resultSet.getString(2));
                this.transferName.setText(resultSet.getString(1));

                transferConfirmationButton.setDisable(false);

                connection.close();

            }else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("The account you entered does not exits\nplease recheck your input data");
                alert.showAndWait();
            }
        } catch (SQLException | NumberFormatException | NullPointerException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("ERROR Please recheck your input data");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void validateTransfer(){

        try{
            if (Double.parseDouble(this.transferAmountTextField.getText()) < checkBalance() - 50){
                if (users.get(0).getBank().getPin() == Integer.parseInt(transferPinVerification.getText())){
                    String sql = "insert into transactions(date,amount)values(?,?)";

                    try {
                        Connection connection = DatabaseConnection.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        preparedStatement.setString(1,getDate());
                        preparedStatement.setDouble(2, Double.parseDouble(this.transferAmountTextField.getText()));

                        preparedStatement.execute();

                        connection.close();

                        insertBalance(1, Integer.parseInt(this.transferDestinationAccount.getText()));
                        insertBalance(3,users.get(0).getBank().getAccountNumber());

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText("");
                        alert.setTitle("REAPER.TEK BANK");
                        alert.setContentText("The transaction has been processed successfully");
                        alert.showAndWait();

                        balanceLabel.setText(String.valueOf(checkBalance()));
                        transferDestinationAccount.clear();
                        transferAmountTextField.clear();
                        transferPinVerification.clear();

                        transferConfirmationButton.setDisable(true);


                        System.out.println("The transfer was successful new balance: $"+checkBalance());

                    } catch (SQLException | NumberFormatException | NullPointerException e) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("");
                        alert.setTitle("REAPER.TEK BANK");
                        alert.setContentText("ERROR Please recheck your input data");
                        alert.showAndWait();
                        e.printStackTrace();
                    }
                }else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("ERROR Please enter the correct pin");
                    alert.showAndWait();
                }

            }else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("Your have insufficient funds to perform this transaction\nyour balance is $\"+checkBalance()");
                alert.showAndWait();
            }
        }catch (NumberFormatException | NullPointerException e ){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("ERROR Please recheck input data");
            alert.showAndWait();
        }
    }

    //////////////////////////////////////////UTILITY METHODS//////////////////////////////////////////////////////////

    private double checkBalance() {
        String sql = "select transactions.amount,balance.type from transactions, balance where balance.account = ? and transactions.reference_number == balance.reference_number";
        List<Balance> balanceList = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, this.users.get(0).getBank().getAccountNumber());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                balanceList.add(new Balance(resultSet.getInt(1),resultSet.getInt(2)));
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        double balance = 0;

        for (Balance bal : balanceList ){
            if (bal.getType() == 1){
                balance += bal.getAmount();
                System.out.println(balance+"-------"+bal.getAmount());
            }
            if (bal.getType() == 2){
                balance -= bal.getAmount();
                System.out.println(balance+"-------"+bal.getAmount());
            }
            if (bal.getType() == 3){
                balance -= bal.getAmount();
            }
        }
        return balance;
    }

    public void insertBalance(int type, int account){
        int reference = 0;

        String sql;

        try {
            Connection connection = DatabaseConnection.getConnection();

            sql = "select reference_number from transactions";

            ResultSet resultSet = connection.createStatement().executeQuery(sql);


            while (resultSet.next()){
                reference = resultSet.getInt(1);
            }

            System.out.println("ref  "+reference+" logged "+account);

            sql = "insert into balance(reference_number,account,type)values(?,?,?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,reference);
            preparedStatement.setInt(2,account);
            preparedStatement.setInt(3,type);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd-yyyy hh:mm:ss");
        return dtf.format(localDateTime);

    }


    ///////////////////////////////////////////BANK STATEMENT///////////////////////////////////////////////////////////

    @FXML
    private TableView<TransactionTableDataModel> transactionTable;

    @FXML
    private TableColumn<TransactionTableDataModel, String> dateColumn;

    @FXML
    private TableColumn<TransactionTableDataModel, Integer> referenceNumberColumn;

    @FXML
    private TableColumn<TransactionTableDataModel, String> transactionTypeColumn;

    @FXML
    private TableColumn<TransactionTableDataModel, Double> transactionAmount;

    private ObservableList<TransactionTableDataModel> transactionData;

    public void setTransactionTable(){
        transactionData = FXCollections.observableArrayList();

        String sql = "select transactions.date,transactions.amount, transaction_type.type, transactions.reference_number from transactions," +
                "transaction_type,balance where balance.reference_number == transactions.reference_number and " +
                "balance.type == transaction_type.id and balance. account = ?";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, this.users.get(0).getBank().getAccountNumber());

            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Amount       type   account");
            while (resultSet.next()){
                transactionData.add(new TransactionTableDataModel(resultSet.getString(1),resultSet.getInt(4),resultSet.getString(3),resultSet.getDouble(2)));
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.referenceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("referenceNumber"));
        this.transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        this.transactionAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        this.transactionTable.setItems(transactionData);
    }

    public void search(){
        String sql = "select transactions.date,transactions.amount, transaction_type.type, transactions.reference_number from transactions," +
                "transaction_type,balance where date like ? and balance.reference_number == transactions.reference_number and " +
                "balance.type == transaction_type.id and balance. account = ? ";
        transactionData.clear();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, "%"+months.getValue().toString().substring(0,3)+"%");
            preparedStatement.setInt(2, this.users.get(0).getBank().getAccountNumber());

            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Amount       type   account");
            while (resultSet.next()){
                transactionData.add(new TransactionTableDataModel(resultSet.getString(1),resultSet.getInt(4),resultSet.getString(3),resultSet.getDouble(2)));
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.referenceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("referenceNumber"));
        this.transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        this.transactionAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        this.transactionTable.setItems(transactionData);
    }

    //////////////////////////////////////////ACCOUNT REGISTRATION/////////////////////////////////////////////////////

    @FXML
    private TextField firstName;

    @FXML
    private TextField middleName;

    @FXML
    private TextField regLastName;

    @FXML
    private TextField nationalID;

    @FXML
    private TextField employer;

    @FXML
    private TextField jobTitle;

    @FXML
    private RadioButton male;

    @FXML
    private RadioButton female;

    @FXML
    private TextArea address;

    @FXML
    private TextArea recoveryPhrase;

    @FXML
    private ComboBox employment;

    @FXML
    private DatePicker dob;

    @FXML
    private PasswordField pin;

    @FXML
    private PasswordField pinVerification;

    public void register(){
        String sql = "insert into users(national_id,first_name,middle_name,last_name," +
                "gender,dob,address,employment_status,employer,job_title,recover_statement)values(?,?,?,?,?,?,?,?,?,?,?)";
        if (pin.getText().equals(pinVerification.getText())){

            String gen;
            if (male.isSelected()){
                gen = "Male";
            }else {
                gen = "Female";
            }

            String mid = middleName.getText();
            if (mid.equals(null))mid = "none";

            String ifEmployerIsnull = employer.getText();
            if (ifEmployerIsnull.equals(null))ifEmployerIsnull = "none";

            String ifJobTitleIsNull = jobTitle.getText();
            if (ifJobTitleIsNull.equals(null))ifJobTitleIsNull = "none";

            try {
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, nationalID.getText());
                preparedStatement.setString(2, firstName.getText());
                preparedStatement.setString(3, mid);
                preparedStatement.setString(4, regLastName.getText());
                preparedStatement.setString(5, gen);
                preparedStatement.setString(6, dob.getEditor().getText());
                preparedStatement.setString(7, address.getText());
                preparedStatement.setString(8, employment.getValue().toString());
                preparedStatement.setString(9, ifEmployerIsnull);
                preparedStatement.setString(10, ifJobTitleIsNull);
                preparedStatement.setString(11, recoveryPhrase.getText());

                preparedStatement.execute();

                sql = "insert into accounts(national_id,pin)values(?,?)";

                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,nationalID.getText());
                preparedStatement.setInt(2, Integer.parseInt(pin.getText()));

                preparedStatement.execute();

                sql = "select account from accounts";

                ResultSet resultSet = connection.createStatement().executeQuery(sql);

                int acc = 0;
                while (resultSet.next()){
                    acc = resultSet.getInt(1);
                }

                connection.close();

                System.out.println("Login with your account number ans pin");

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("Registration successful");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("Account details\n" +
                        "first name: "+firstName.getText()+"\nlast name: "+regLastName.getText()+"\nAccount number: "+(acc)+"\n\nnow login with your account number\n" +
                        " and pin entered during rgistration");
                alert.showAndWait();

                this.firstName.clear();
                this.middleName.clear();
                this.regLastName.clear();
                this.pinVerification.clear();
                this.pin.clear();
                this.recoveryPhrase.clear();
                this.address.clear();
                this.employer.clear();
                this.jobTitle.clear();
                welcomePane.toFront();

            } catch (SQLException | NullPointerException | NumberFormatException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("ERROR Please recheck your input data \nand make sure you fill all required fields");
                alert.showAndWait();
                e.printStackTrace();
            }
        }else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("ERROR The pin number's dont match \n" +
                    " Please reenter the pin's");
            alert.showAndWait();
            pin.clear();
            pinVerification.clear();
        }
    }

    /////////////////////////////////////////ACCOUNT DEACTIVATION//////////////////////////////////////////////////////

    @FXML
    private TextField deactivateAcc;

    @FXML
    private PasswordField deactivationPin;

    @FXML
    private TextField deactivateWithdrawField;

    @FXML
    private TextField deactivateTransferAcc;

    @FXML
    private TextField deactivateTransferAmt;

    @FXML
    private Button deWithdrawOk;

    @FXML
    private Button deTransferOk;

    @FXML
    private Button deTransferVerify;

    @FXML
    private Button deactivationCancel;

    @FXML
    private Button deactivationConfirm;

    @FXML
    private Label deName;

    @FXML
    private Label deLastName;

    @FXML
    private Label deAccount;

    @FXML
    private Label accountBalance;

    @FXML
    private RadioButton withdrawRadio;

    @FXML
    private RadioButton transferRadio;

    public void deactivationAccountVerification(){
        if (Integer.parseInt(deactivateAcc.getText()) == users.get(0).getBank().getAccountNumber() && Integer.parseInt(deactivationPin.getText()) == users.get(0).getBank().getPin()){
            deactivateWithdrawField.setEditable(true);
            deactivateTransferAcc.setEditable(true);
            deactivateTransferAmt.setEditable(true);
            deTransferOk.setDisable(false);
            deWithdrawOk.setDisable(false);
            deTransferVerify.setDisable(false);
            deactivationConfirm.setDisable(false);
            transferRadio.setDisable(false);
            withdrawRadio.setDisable(false);
            accountBalance.setText(String.valueOf(checkBalance()));
        }else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("Incorrect pin \nrecheck your input data");
            alert.showAndWait();        }
    }

    @FXML
    private void deactivateWithdrawal() {

        if (withdrawRadio.isSelected()){
            if (checkBalance() - 50  >  Double.parseDouble(deactivateWithdrawField.getText())){
                String sql = "insert into transactions(date,amount)values(?,?)";

                try {
                    Connection connection = DatabaseConnection.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setString(1,getDate());
                    preparedStatement.setDouble(2,Double.parseDouble(deactivateWithdrawField.getText()));

                    preparedStatement.execute();

                    connection.close();

                    insertBalance(2,this.users.get(0).getBank().getAccountNumber());

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("You have successfully withdrawn $"+this.deactivateWithdrawField.getText()+"\nnew balance $"+checkBalance());
                    alert.showAndWait();
                    deactivateWithdrawField.clear();

                } catch (SQLException | NumberFormatException | NullPointerException e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("Please Fill in all Fields and \nrecheck your input data");
                    alert.showAndWait();
                    e.printStackTrace();
                }

            }else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("You have insufficient funds to\nprocess this transaction (Including bank Charges)");
                alert.showAndWait();
            }
        }else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("You need to select the withdrawal option)");
            alert.showAndWait();
        }
    }

    @FXML
    private void deactivationTransferMoney() {

        if (transferRadio.isSelected()){
            String sql = "select national_id from accounts where account = ?";

            try {
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setInt(1, Integer.parseInt(this.deactivateTransferAcc.getText()));

                ResultSet resultSet = preparedStatement.executeQuery();

                String id = resultSet.getString(1);

                if (!id.equals(null)){
                    sql = "select users.first_name,users.last_name,accounts.account from users,accounts where accounts.national_id = ? and accounts.national_id == users.national_id";

                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, id);

                    resultSet = preparedStatement.executeQuery();

                    System.out.println("Transfering money to "+resultSet.getString(1)+" "+resultSet.getString(2)+" Account number: "+account);

                    this.deAccount.setText(resultSet.getString(3));
                    this.deLastName.setText(resultSet.getString(2));
                    this.deName.setText(resultSet.getString(1));

                    connection.close();

                }else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("The account you entered does not exits\nplease recheck your input data");
                    alert.showAndWait();
                }
            } catch (SQLException | NumberFormatException | NullPointerException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("ERROR Please recheck your input data");
                alert.showAndWait();
                e.printStackTrace();
            }
        }else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("You need to select the transfer option)");
            alert.showAndWait();
        }
    }

    public void DeactivationValidateTransfer(){

        try{
            if (Double.parseDouble(this.deactivateTransferAmt.getText()) < checkBalance() - 50){
                String sql = "insert into transactions(date,amount)values(?,?)";

                try {
                    Connection connection = DatabaseConnection.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setString(1,getDate());
                    preparedStatement.setDouble(2, Double.parseDouble(this.deactivateTransferAmt.getText()));

                    preparedStatement.execute();

                    connection.close();

                    insertBalance(1, Integer.parseInt(this.deactivateTransferAcc.getText()));
                    insertBalance(3,users.get(0).getBank().getAccountNumber());

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("The transaction has been processed successfully");
                    alert.showAndWait();

                    deactivateTransferAmt.clear();
                    deactivateTransferAcc.clear();

                    System.out.println("The transfer was successful new balance: $"+checkBalance());

                } catch (SQLException | NumberFormatException | NullPointerException e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("");
                    alert.setTitle("REAPER.TEK BANK");
                    alert.setContentText("ERROR Please recheck your input data");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("");
                alert.setTitle("REAPER.TEK BANK");
                alert.setContentText("Your have insufficient funds to perform this transaction\n(including bank charges)");
                alert.showAndWait();
            }
        }catch (NumberFormatException | NullPointerException e ){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("");
            alert.setTitle("REAPER.TEK BANK");
            alert.setContentText("ERROR Please recheck input data");
            alert.showAndWait();
        }
    }

    public void deactivationAlertDialogue(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("");
        alert.setTitle("REAPER.TEK BANK");
        alert.setContentText("After deactivation \n1. if you had any money in your account you \nwont be able to access it permanently" +
                "\n2. You wont be able to login again with your previous credentials\nyou will have to register a new account\n\nSelect ok to continue with deactivation" +
                "or click cancel ");
        alert.showAndWait();

        if (alert.getResult().getButtonData().isDefaultButton()){
            confirmDeactivation();
        }

    }

    public void confirmDeactivation(){
        String sql = "delete from accounts where account = ?";
        try{
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,this.users.get(0).getBank().getAccountNumber());

            preparedStatement.execute();

            connection.close();

            users.clear();
            planePane.toFront();
            welcomePane.toFront();

            System.out.println("Your account hs been successfully deactivated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelDeactivation(){
        deactivateWithdrawField.clear();
        deactivateTransferAcc.clear();
        deactivateTransferAmt.clear();
        deactivateAcc.clear();
        deactivationPin.clear();

        deactivateWithdrawField.setEditable(false);
        deactivateTransferAcc.setEditable(false);
        deactivateTransferAmt.setEditable(false);
        deTransferOk.setDisable(true);
        deWithdrawOk.setDisable(true);
        deTransferVerify.setDisable(true);
        deactivationConfirm.setDisable(true);
        transferRadio.setDisable(true);
        withdrawRadio.setDisable(true);

    }


    /////////////////////////////////////////APP PANELS ARRANGEMENT////////////////////////////////////////////////////
    @FXML
    private Pane formPane;

    @FXML
    private Pane planePane;

    @FXML
    private Pane paneDashboard;

    @FXML
    private Pane depositPane;

    @FXML
    private Pane withdrawPane;

    @FXML
    private Pane transferPane;

    @FXML
    private Pane bankStatementPane;

    @FXML
    private Pane deactivateCardPane;

    @FXML
    private Pane welcomePane;

    @FXML
    private Pane loginPane;

    @FXML
    private Pane registerPane;

    @FXML
    private Button register;

    @FXML
    private Button login;

    @FXML
    private Button cancelRegistration;

    @FXML
    private  Button home;

    @FXML
    private  Button depositButton;

    @FXML
    private  Button withdrawalButton;

    @FXML
    private  Button transferButton;

    @FXML
    private  Button bankStatementButton;

    @FXML
    private  Button deactivateButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Pane rrr;

    public void test(){
        rrr.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                rrr.resize(400, 300);
            }
        });
        rrr.isResizable();
        rrr.resize(400, 300);
    }

    public void paneArrangement(ActionEvent event){
        if (event.getSource().equals(register)){
            registerPane.toFront();
        }else if (event.getSource().equals(login)){
            loginPane.toFront();
        }else if (event.getSource().equals(cancelRegistration)){
            welcomePane.toFront();
        }else if (event.getSource().equals(home)){
            welcomePane.toFront();
        }else if (event.getSource().equals(depositButton)){
            depositPane.toFront();
        }else if (event.getSource().equals(withdrawalButton)){
            withdrawPane.toFront();
        }else if (event.getSource().equals(transferButton)){
            transferPane.toFront();
            transferConfirmationButton.setDisable(true);
        }else if (event.getSource().equals(bankStatementButton)){
            setTransactionTable();
            bankStatementPane.toFront();
        }else if (event.getSource().equals(deactivateButton)){
            deactivateCardPane.toFront();
        }else if (event.getSource().equals(logoutButton)){
            planePane.toFront();
            welcomePane.toFront();
            users.clear();
        }else if (event.getSource().equals(deactivationCancel)){
            this.paneDashboard.toFront();
            cancelDeactivation();
        }
    }
}

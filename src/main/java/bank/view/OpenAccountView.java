
package bank.view;

import bank.controller.Controllers;
import bank.dto.AccountSummary;
import bank.dto.CustomerSummary;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;



public class OpenAccountView extends VBox {
   
    private static final String SAVINGS = "Savings";
    private static final String INVESTMENT = "Investment";
    private static final String CHEQUE = "Cheque";

    private final Controllers controllers;
    private final ComboBox<CustomerSummary> customer =new ComboBox<>();
    private final ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList(SAVINGS, INVESTMENT, CHEQUE));
    private final ComboBox<String> branchName = new ComboBox<>(FXCollections.observableArrayList("Gaborone Game City", "Francistown", "Kanye"));
    private final TextField deposit = new TextField();
    private final TextField employerName = new TextField();
    private final TextField employerAddress = new TextField();
    private final Label employerNote = new Label("A Cheque account is for salaried individuals, so an employer is required.");
    private final Label employerNameLabel = new Label("Employer name");
    private final Label employerAddressLabel = new Label("Employer address");
    private final Label status = Ui.status();

    public OpenAccountView(Controllers controllers) {
        this.controllers = controllers;
        setSpacing(9);
        setPadding(new Insets(12));

        customer.setPrefWidth(310);
        type.getSelectionModel().selectFirst();
        branchName.getSelectionModel().selectFirst();
        deposit.setPromptText("500.00");


    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(8);
    form.addRow(0, new Label("Customer"), customer);
    form.addRow(1, new Label("Account Type"), type);
    form.addRow(2, new Label("Branch"), branchName);
    form.addRow(3, new Label("Opening deposit (BWP)"), deposit);
    form.addRow(4, employerNameLabel, employerName);
    form.addRow(5, employerAddressLabel, employerAddress);


    Label rules = new Label("Take Note: One account of each type per customer. Investment needs at least BWP 500.00 "
                + "to open and can never drop below it. Companies cannot hold Cheque accounts.");
        rules.setWrapText(true);

        Button open = new Button("Open account");
        open.setOnAction(event -> open());
        type.valueProperty().addListener((obs, old, now) -> showEmployer());
        showEmployer();

        getChildren().addAll(Ui.heading("Open an account"), form, employerNote, rules, open, status);


    }
   
     private void showEmployer() {
        boolean cheque = CHEQUE.equals(type.getValue());
        for (javafx.scene.Node node : new javafx.scene.Node[] {employerName, employerAddress, employerNote,
                employerNameLabel, employerAddressLabel}) {
            node.setVisible(cheque);
            node.setManaged(cheque);
        }
    }

 void refresh() {
   Ui.guard(status, () -> {
    CustomerSummary selected = customer.getValue();
       customer.setItems(FXCollections.observableArrayList(controllers.customers().list()));
         if (selected != null) {
             customer.getItems().stream().filter(c -> c.customerId().equals(selected.customerId()))
             .findFirst().ifPresent(customer::setValue);
            }
        });
    }


    private void open() {
        Ui.guard(status, () -> {
            CustomerSummary who = customer.getValue();
            if (who == null) {
                Ui.error(status, "Choose a customer first.");
                return;
            }
            String id = who.customerId();
            AccountSummary opened = switch (type.getValue()) {
                case INVESTMENT -> controllers.accounts().openInvestment(id, branchName.getValue(), deposit.getText());
                case CHEQUE -> controllers.accounts().openCheque(id, branchName.getValue(), deposit.getText(),
                        employerName.getText(), employerAddress.getText());
                default -> controllers.accounts().openSavings(id, branch.getValue(), deposit.getText());
            };
            Ui.success(status, "Opened " + opened + " for " + who.displayName() + ".");
            deposit.clear();
            employerName.clear();
            employerAddress.clear();
        });
    }
 
    

}

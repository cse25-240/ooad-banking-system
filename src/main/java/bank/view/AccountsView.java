
package bank.view;

import java.time.format.DateTimeFormatter;

import bank.controller.Controllers;
import bank.dto.AccountSummary;
import bank.dto.SessionDetails;
import bank.dto.TransactionRow;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;




/**
 *
 * @author codespace
 */
public class AccountsView extends VBox {
    private static final DateTimeFormatter UPON = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm");

    private final Controllers controllers;
    private final TableView<AccountSummary> accounts = new TableView<>();
    private final TableView<TransactionRow> statement = new TableView<>();
    private final TextField amount = new TextField();
    private final TextField description = new TextField();
    private final Button deposit = new Button("Deposit");
    private final Button withdraw = new Button("Withdraw");
    private final Label status = Ui.status();


    AccountsView(Controllers controllers, SessionDetails who) {
        this.controllers = controllers;
        setSpacing(8);
        setPadding(new Insets(10));

   

    accounts.getColumns().add(Ui.column("Account no.", AccountSummary::accountNumber, 130));
        accounts.getColumns().add(Ui.column("Type", AccountSummary::accountType, 90));
        accounts.getColumns().add(Ui.column("Branch", AccountSummary::branch, 150));
        accounts.getColumns().add(Ui.moneyColumn("Balance (BWP)", AccountSummary::balance, 120));
        accounts.getColumns().add(Ui.column("Opened", a -> a.openedOn().toString(), 100));
        if (who.staffMember()) {
            accounts.getColumns().add(Ui.column("Owner", AccountSummary::ownerId, 110));    
}
accounts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

statement.getColumns().add(Ui.column("Upon", t -> t.timestamp().format(UPON), 150));
statement.getColumns().add(Ui.column("Type", TransactionRow::type, 100));
statement.getColumns().add(Ui.moneyColumn("Amount", TransactionRow::amount, 100));
statement.getColumns().add(Ui.moneyColumn("Balance after", TransactionRow::balanceAfter, 120));
statement.getColumns().add(Ui.column("Description", TransactionRow::description, 220));
statement.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

amount.setPromptText("Amount (BWP)");
 description.setPromptText("Description (optional)");
    HBox.setHgrow(description, Priority.ALWAYS);
    Button refresh = new Button("Refresh");
    refresh.setOnAction(event -> reload());
    deposit.setOnAction(event -> transact(true));
    withdraw.setOnAction(event -> transact(false));
    deposit.setDisable(true);
    withdraw.setDisable(true);

  accounts.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> onSelected(now));
  
HBox actions = new HBox(8, amount, description, deposit, withdraw, refresh);
     VBox.setVgrow(accounts, Priority.ALWAYS);
    VBox.setVgrow(statement, Priority.ALWAYS);
    getChildren().addAll(Ui.heading(who.staffMember() ? "All accounts" : "My accounts"), accounts, actions, status,
        Ui.heading("Statement (newest first)"), statement);
        reload();  


}

   void reload(){
    Ui.guard(status, () -> {
        String retain = selectedNumber();
        accounts.setItems(FXCollections.observableArrayList(controllers.accounts().list()));
        if (retain != null){
            accounts.getItems().stream().filter(a -> a.accountNumber().equals(retain)).findFirst()
                        .ifPresent(a -> accounts.getSelectionModel().select(a));
        }
    });
   }
  private String selectedNumber(){
    AccountSummary selected = accounts.getSelectionModel().getSelectedItem();
    return selected == null ? null : selected.accountNumber();
  }

  private void onSelected(AccountSummary account) {
        deposit.setDisable(account == null);
        withdraw.setDisable(account == null || !account.canWithdraw());
        withdraw.setTooltip(account != null && !account.canWithdraw()
                ? new Tooltip(account.accountType() + " accounts do not allow withdrawals.") : null);
        if (account == null) {
            statement.getItems().clear();
            return;
        }
        Ui.guard(status, () -> statement.setItems(
                FXCollections.observableArrayList(controllers.transactions().history(account.accountNumber()))));
    }
private void transact(boolean isDeposit) {
        Ui.guard(status, () -> {
            String number = selectedNumber();
            if (number == null) {
                Ui.error(status, "Account selection required.");
                return;
            }
    AccountSummary after = isDeposit
            ? controllers.transactions().deposit(number, amount.getText(), description.getText())
            : controllers.transactions().withdraw(number, amount.getText());
            amount.clear();
            description.clear();
            reload();
            Ui.success(status, (isDeposit ? "Deposited. " : "Withdrawn. ") + "New balance: BWP " + Ui.money(after.balance()));
        });


}}
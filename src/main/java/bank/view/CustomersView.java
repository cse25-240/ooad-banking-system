

package bank.view;

import bank.controller.Controllers;
import bank.dto.CustomerSummary;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;

/**
 *
 * @author codespace
 */
public class CustomersView extends SplitPane { 
  private final Controllers controllers;
  private final TableView<CustomerSummary> table = new TableView<>();
  private final Label status = Ui.status();

  

CustomersView(Controllers controllers) {
     
  this.controllers = controllers;
  setOrientation(Orientation.VERTICAL); 
   
  table.getColumns().add(Ui.column("Customer ID", CustomerSummary::customerId, 110));
  table.getColumns().add(Ui.column("Type", CustomerSummary::customerType, 90)); 
  table.getColumns().add(Ui.column("Name", CustomerSummary::displayName, 220));
  table.getColumns().add(Ui.column("ID / Reg. no.", CustomerSummary::omang, 130));
  table.getColumns().add(Ui.column("Phone", CustomerSummary::phoneNumber, 110));
  table.getColumns().add(Ui.column("Accounts", c -> String.valueOf(c.accountCount()), 80));
  table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);

  VBox top = new VBox(6, Ui.heading("Customers"), table, status);
  top.setPadding(new Insets(10));
  VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS); 

  getItems().addAll(top, new RegisterCustomerView(controllers, this::refresh));
  setDividerPositions(0.45);
  refresh();

}
 
void refresh(){
    Ui.guard(status, () -> table.setItems(FXCollections.observableArrayList(controllers.customers().list())));
}



}
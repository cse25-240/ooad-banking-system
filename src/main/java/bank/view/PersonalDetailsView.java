package bank.view;

import bank.controller.Controllers;
import bank.dto.CustomerSummary;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class PersonalDetailsView extends VBox{
 
    private final Conntrollers controllers;
    private final Label name = new Label();
    private final Label identity = new Label();
    private final TextField address = new TextField();
    private final TextField phone = new TextField();
    private final Label status = Ui.status();
    private String customerId;

    public PersonalDetailsView(Controllers controllers) {
        this.controllers = controllers;
     setSpacing(10);
     setPadding(new Insets(14));
     GridPane form = new GridPane();
     form.setHgap(10);
     form.setVgap(8);
     form.addRow(0, new Label("Name"), name);
     form.addRow(1, new Label("OMANG"), identity);
     form.addRow(2, new Label("Address"), address);
     form.addRow(3, new Label("Phone"), phone);
     address.setPrefWidth(320);
     Button save = new Button("Save contact details");
     save.setOnAction(event -> Ui.guard(status, () -> {
         display(controllers.customers().updateContact(customerId, address.getText(), phone.getText()));
        Ui.success(status, "Saved.");
        })); 
    getChildren().addAll(Ui.heading("My details"), form, save, status);
        reload();
    
        
    }

void reload(){
    Ui.guard(status, () -> {
            var mine = controllers.customers().list();
            if (!mine.isEmpty()) {
                display(mine.get(0));
            }
        }); 
}    

private void display(CustomerSummary y){
    customerId = y.customerId();
    name.setText(y.displayName() + "(" + y.customerType() + ")");
    identity.setText(y.identityNumber());
    address.setText(y.address());
    phone.setText(y.phoneNumber());
}

    
    
}

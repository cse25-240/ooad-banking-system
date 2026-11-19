/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package bank.view;

import bank.controller.Controllers;
import bank.dto.CustomerSummary;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 
 * this is the form where staff registers an individual or company customer with the option of a login
 */
public class RegisterCustomerView extends VBox {

    private final Controllers controllers;
    private final Runnable inRegistered;

   private final RadioButton individual = new RadioButton("Individual");
   private final RadioButton company = new RadioButton("company");
   
   private final TextField firstName = new TextField();
   private final TextField lastName = new TextField();
   private final TextField nationalId = new TextField();
   private final TextField companyName = new TextField();
   private final TextField registrationNumber = new TextField();
   private final TextField contactOfficer = new TextField();
   private final TextField address = new TextField();
   private final TextField phoneNumber = new TextField();
   private final TextField username = new TextField();
   private final PasswordField password = new PasswordField();
   private final Label status = Ui.status();

   private final GridPane individualFields = new GridPane();
   private final GridPane companyFields = new GridPane();

   RegisterCustomerView(Controllers controllers, Runnable inRegistered) {
        this.controllers = controllers;
        this.inRegistered = inRegistered;
        setSpacing(7);
        setPadding(new Insets(9));

        ToggleGroup type = new ToggleGroup();
        individual.setToggleGroup(type);
        company.setToggleGroup(type);
        individual.setSelected(true);

        addRow(individualFields,0, "First Name", firstName);
        addRow(individualFields,1, "Last Name", lastName);
        addRow(individualFields,2, "Omang", nationalId);
        addRow(companyFields,0, "Company name", companyName);
        addRow(companyFields,1, "Registration Number", registrationNumber);
        addRow(companyFields,2, "Contact Officer", contactOfficer);

        GridPane shared = new GridPane();
        addRow(shared, 0, "Address", address);
        addRow(shared, 1, "Phone Number", phoneNumber);
         
        GridPane login = new GridPane();
        addRow(login, 0, "Login username(optional)", username);
        addRow(login, 1, "Login Password", password);

        Button register = new Button("Register Customer");
        register.setOnAction(Event -> register());

        type.selectedToggleProperty().addListener((obs, old, now) -> showFields());
        showFields();

        getChildren().addAll(Ui.heading("Register Customer"), new HBox(12, individual, company), 
    individualFields, companyFields, shared, login, register, status);

    }

   private static void addRow(GridPane grid, int row, String Label, javafx.scene.Node field) {
    grid.setHgap(9);
    grid.setVgap(7);
    Label caption = new Label();
    caption.setMinWidth(170);
    grid.add(caption, 0, row);
    grid.add(field, 1, row);
   }

  private void showFields(){
    boolean person = individual.isSelected();
    individualFields.setVisible(person);
    individualFields.setManaged(person);
    companyFields.setVisible(!person);
    companyFields.setManaged(!person);
  } 

   private void register(){
    Ui.guard(status, () -> {
        CustomerSummary created = individual.isSelected()
        ? controllers.customers().registerIndividual(firstName.getText(), lastName.getText(), nationalId.getText(),
                                                      address.getText(), phoneNumber.getText())
        : controllers.customers().registerCompany(companyName.getText(), registrationNumber.getText(), contactOfficer.getText(),
                                                   address.getText(), phoneNumber.getText());
    String message = "Registered" + created + ".";
    if (!username.getText().isBlank()|| !password.getText().isEmpty()){
        try {
            controllers.customers().createLogin(created.customerId(), username.getText(), password.getText());

        } catch (bank.exception.BankingException r) {
            Ui.error(status, created.customerId() + "has already been registered, but login is missing: "+ r.getMessage());
            clearFields();
            inRegistered.run();
            return;
        }
    } 
    clearFields();
    Ui.success(status, message);
    inRegistered.run();                                                                                            
    });
   

   }
   private void clearFields() {
    for (TextField field : new TextField[] {firstName, lastName, nationalId, companyName, registrationNumber, contactOfficer,
        address, phoneNumber, username, password
    }) { 
        field.clear();
    }
   }

 



 



}

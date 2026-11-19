/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package bank.view;

import bank.controller.Controllers;
import bank.dto.SessionDetails;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 *
 * @author codespace
 */
public class MainView extends BorderPane {

    MainView(Controllers controllers, SessionDetails who, Runnable onLogout) {
     Label user = new Label(who.username() + "(" + who.role().toLowerCase() + ")");
     user.getStyleClass().add("user");
     Label title = new Label("Banking System");
     title.getStyleClass().add("title");
     Region spacer = new Region();
     HBox.setHgrow(spacer, Priority.ALWAYS);
     Button logout = new Button("Sign out");
     logout.setOnAction(event -> {
     controllers.login().logout();
     onLogout.run();
        }); 
        
    HBox bar = new HBox(12, title, spacer, user, logout);
    bar.setAlignment(Pos.CENTER_LEFT);   
    bar.setPadding(new Insets(10, 14, 10, 14));
    bar.getStyleClass().add("bar");
    setTop(bar);
    
    TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        if (who.staffMember()) {
        CustomersView customers = new CustomersView(controllers);
        OpenAccountView open = new OpenAccountView(controllers);
        AccountsView accounts = new AccountsView(controllers, who);
        InterestView interest = new InterestView(controllers);
        tabs.getTabs().addAll(tab("Customers", customers, customers::refresh),
            tab("Open account", open, open::refresh),
            tab("Accounts & transactions", accounts, accounts::reload),
            tab("Interest", interest, () -> { }));
        } else {
            AccountsView accounts = new AccountsView(controllers, who);
            PersonalDetailsView details = new PersonalDetailsView(controllers);
            tabs.getTabs().addAll(tab("My accounts", accounts, accounts::reload),
                    tab("My details", details, details::reload));
        }
        setCenter(tabs);

    }

    private static Tab tab(String title, javafx.scene.Node content, Runnable onShown) {
    Tab tab = new Tab(title, content);
    tab.setOnSelectionChanged(event -> {
     if (tab.isSelected()) {
        onShown.run();
     }
 });
     return tab;
    } 


    

}

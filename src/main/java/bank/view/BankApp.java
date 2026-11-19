package bank.view;

import bank.app.AppContext;
import bank.controller.Controllers;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


public class BankApp extends Application{

    private Controllers controllers;
    private Stage stage;

    @Override 
    public void start(Stage mainStage){
        this.stage=mainStage;
        try{
            controllers = AppContext.h2("./data/bank");

        }catch(RuntimeException e){
            new Alert(Alert.AlertType.ERROR, "Database Could not be opened: " + e.getMessage()).showAndWait();
            Platform.exit();
            return;
        }
        stage.setTitle("Banking System");
        displayLogin();
        stage.show();
    }
  
    private void displayLogin(){
        show(new LoginView(controllers,info -> show(new MainView(controllers, info, this::displayLogin))), 480, 420);

    }
    

    private void show(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        var css = BankApp.class.getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void show(Parent root) {
        show(root, 1040, 720);
    }

    public static void main(String[] args) {
        launch(args);



}
}

package bank.view;

import bank.controller.Controllers;
import bank.dto.SessionDetails;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 *
 * @author codespace
 */
public class LoginView extends VBox{

 LoginView(Controllers controllers, Consumer<SessionDetails> inSignedIn) {
 setAlignment(Pos.CENTER);
 setSpacing(10);
 setPadding(new Insets(40));
 getStyleClass().add("login");
 
  Label title = new Label("Banking System");
  title.getStyleClass().add("title");
  Label subtitle = new Label("Sign in to continue");

 TextField username = new TextField();
    username.setPromptText("Username");
    username.setMaxWidth(280);
 PasswordField password = new PasswordField();
password.setPromptText("Password");
password.setMaxWidth(280);
Button signIn = new Button("Sign in");
 signIn.setDefaultButton(true);
 signIn.setMaxWidth(280);
 Label status = Ui.status();
 status.setMaxWidth(280);

        signIn.setOnAction(event -> Ui.guard(status, () -> {
            SessionDetails info = controllers.login().login(username.getText(), password.getText());
            password.clear();
            Ui.clear(status);
            inSignedIn.accept(info);
        }));

        getChildren().addAll(title, subtitle, username, password, signIn, status);
    }





}






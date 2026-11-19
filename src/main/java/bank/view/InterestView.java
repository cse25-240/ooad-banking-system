
package bank.view;

import bank.controller.Controllers;
import bank.dto.InterestSummary;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;




/**
 *
 * @author codespace
 */
public class InterestView extends VBox{
 InterestView(Controllers controllers) {
 setSpacing(10);
 setPadding(new Insets(14));

 List<YearMonth> months = new ArrayList<>();
 YearMonth month = YearMonth.now();
 for (int i = 0; i < 12; i++) {
   months.add(month.minusMonths(i));
 }
  ComboBox<YearMonth> choice = new ComboBox<>(FXCollections.observableArrayList(months));
  choice.getSelectionModel().select(months.size() > 1 ? 1 : 0);
  Button run = new Button("Pay interest");
  Label status = Ui.status();
  
  
Label rules = new Label("Savings Accounts earn 0.05% per annum and Investment Accounts earn 5% per annum, added interest."+
    "Cheque accounts do not earn interest."
);
rules.setWrapText(true);

run.setOnAction(event -> Ui.guard(status, () -> {
            InterestSummary result = controllers.interest().run(choice.getValue());
            Ui.success(status, result.month() + ": paid " + result.accountsPaid() + " account(s), BWP "
                    + Ui.money(result.totalInterest()) + " in total; "
                    + result.accountsAlreadyPaid() + " already paid.");
        }));

        getChildren().addAll(Ui.heading("Month-end interest"), rules, new HBox(10, new Label("Month"), choice, run), status);




}



}

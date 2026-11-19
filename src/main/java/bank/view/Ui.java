package bank.view;

import bank.exception.BankingException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.Function;




final class Ui{

    interface Action{
        void run() throws BankingException;
    }

private Ui(){

}

static void guard(Label status, Action action){
    try{
        action.run();
    } catch(BankingException r){
        error(status, r.getMessage());
    } catch(RuntimeException e){
        error(status, "Something went wrong"+ e.getMessage());
    }
}



static void error(Label status, String message) {
    status.getStyleClass().setAll("status","error");
    status.setText(message);
}
 
static void success(Label status, String message){
    status.getStyleClass().setAll("status","success");
    status.setText(message);
}

static void clear(Label status){
    status.getStyleClass().setAll("status");
    status.setText("");
}

static Label heading(String text){
    Label label = new Label(text);
    label.getStyleClass().add("heading");
    return label;
}

static Label status() {
    Label label =new Label();
    label.getStyleClass().add("Status");
    label.setWrapText(true);
    return label;
}

static String money(BigDecimal amount) {
    return String.format(Locale.UK,"%,.2f",amount);

}



static <T> TableColumn<T, String> column(String title,Function<T, String> value, double width){
  TableColumn<T, String> column = new TableColumn<>(title);
  column.setCellValueFactory(Cell -> new SimpleStringProperty(value.apply(Cell.getValue())));
  column.setPrefWidth(width);
  return column;

}



static <T> TableColumn<T, String> moneyColumn(String title, Function<T, BigDecimal> value, double width){
    TableColumn<T, String> column = column(title, row -> money(value.apply(row)), width);
    column.getStyleClass().add("right");
    return column;
}



}


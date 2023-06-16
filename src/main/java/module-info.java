module ru.funfayk082.currencyconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens ru.funfayk082.currencyconverter to javafx.fxml;
    exports ru.funfayk082.currencyconverter;
}
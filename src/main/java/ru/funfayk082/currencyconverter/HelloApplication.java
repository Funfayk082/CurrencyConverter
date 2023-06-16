package ru.funfayk082.currencyconverter;

import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class HelloApplication extends Application {
    private static final int CONNECTION_TIMEOUT = 3000;
    private static final String TAG_DATA = "data";
    private static final String TAG_VALUE = "value";
    TextField tf1 = new TextField();
    Label tf2 = new Label();
    ComboBox<String> cb1 = new ComboBox();
    ComboBox<String> cb2 = new ComboBox();
    String[] currencies = {"USD", "RUB", "EUR"};

    @Override
    public void start(Stage stage) throws IOException {

        Button btn = new Button("Посчитать");
        cb1.getItems().setAll("Доллар", "Рубль", "Евро");
        cb2.getItems().setAll("Доллар", "Рубль", "Евро");

        GridPane root = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(150, 150, Double.MAX_VALUE);
        column1.setHgrow(Priority.ALWAYS);
        root.getColumnConstraints().add(column1);
        ColumnConstraints column2 = new ColumnConstraints(150, 150, Double.MAX_VALUE);
        column1.setHgrow(Priority.ALWAYS);
        root.getColumnConstraints().add(column2);

        root.getRowConstraints().add(new RowConstraints(80));
        root.getRowConstraints().add(new RowConstraints(80));
        root.getRowConstraints().add(new RowConstraints(80));
        tf1.setAlignment(Pos.CENTER);

        root.setGridLinesVisible(true);
        GridPane.setColumnIndex(tf1, 0);
        GridPane.setColumnIndex(tf2, 1);
        GridPane.setColumnSpan(root, 2);
        //root.getChildren().addAll(tf1, tf2);
        root.add(tf1, 0, 0);
        column1.setHalignment(HPos.CENTER);
        column2.setHalignment(HPos.CENTER);
        root.add(tf2, 1, 0);
        root.add(cb1, 0, 1);
        root.add(cb2, 1, 1);
        root.add(btn, 0, 2);
        String a = tf1.getText();
        btn.setOnAction(new ButtonListener());
        Scene scene = new Scene(root, 330, 240);
        stage.setTitle("Конвертер валюты");
        stage.setScene(scene);
        stage.show();

        tf1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf1.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }

    class ButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Double count;
            String currency = new String();
            String base = new String();
            switch (cb1.getValue()){
                case "Доллар":
                    currency = currencies[0].toString();
                    break;
                case "Рубль":
                    currency = currencies[1].toString();
                    break;
                case "Евро":
                    currency = currencies[2].toString();
            }
            switch (cb2.getValue()){
                case "Доллар":
                    base = currencies[0].toString();
                    break;
                case "Рубль":
                    base = currencies[1].toString();
                    break;
                case "Евро":
                    base = currencies[2].toString();
            }
            try {
                count = Double.parseDouble(tf1.getText())/ getJSONString(getData(currency , base), currency);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            tf2.setText(String.valueOf(Math.ceil(count*1000)/1000));
        }
    }

    public String getData(String currency, String base) throws IOException {
        final URL url = new URL("https://api.currencyapi.com/v3/latest?apikey=CXerELlzvZ0jDFSUj8LIUECZsfv1rS317RehMuNB&currencies="+currency+"&base_currency="+base);
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(CONNECTION_TIMEOUT);
        con.setReadTimeout(CONNECTION_TIMEOUT);
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public Double getJSONString(String json, String currency) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject rootJO = (JSONObject) parser.parse(json);
        JSONObject curJsonObj = (JSONObject) rootJO.get(TAG_DATA);
        JSONObject curV = (JSONObject) curJsonObj.get(currency);
        Double curValue = (Double) curV.get(TAG_VALUE);
        return curValue;
    }
}
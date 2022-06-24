package com.darksoft.banco_taller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    JSONArray listaArrayMovimientos;

    @FXML
    private ListView<String> lvUltimosMoviemientos;
    @FXML
    private BorderPane bp;

    @FXML
    private Button btnBancos;

    @FXML
    private Button btnCiudad;

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnSalir;

    @FXML
    private Button btnSocios;

    @FXML
    private AnchorPane contenedor;

    @FXML
    private void buttons(ActionEvent event) {
        if (event.getSource() == btnInicio) {
            bp.setCenter(contenedor);
        }
        if (event.getSource() == btnSocios) {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("Socios.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            bp.setCenter(root);
        }
        if (event.getSource() == btnBancos) {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("Bancos.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            bp.setCenter(root);
        }
        if (event.getSource() == btnCiudad) {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("Ciudad.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            bp.setCenter(root);
        }
        if (event.getSource() == btnSalir) {
            System.exit(0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        obtenerMovimientos();
    }

    private void obtenerMovimientos(){
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().
                uri(URI.create("http://localhost:8080/movimientos")).
                header("Conten-Type", "application/json").
                GET().build();

        String respuesta = cliente.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        JSONObject movimientos = new JSONObject(respuesta);

        listaArrayMovimientos = movimientos.optJSONObject("_embedded").optJSONArray("Movimiento");

        lvUltimosMoviemientos.getItems().clear();

        JSONArray a = new JSONArray(listaArrayMovimientos);
        for (int i = 0; i < a.length(); i++){

            String fechaHora = a.getJSONObject(i).getString("fechaHora");
            String tipo = a.getJSONObject(i).getString("tipo");
            String monto = a.getJSONObject(i).getString("monto");

            lvUltimosMoviemientos.getItems().add(fechaHora + "\t" + tipo + "\t" + monto);

        }
    }
}
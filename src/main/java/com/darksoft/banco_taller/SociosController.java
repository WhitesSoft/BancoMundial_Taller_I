package com.darksoft.banco_taller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class SociosController implements Initializable {

    @FXML
    private Button btnAgregarSocio;

    @FXML
    private TextField btnCuenta;

    @FXML
    private ComboBox<?> cbBanco;

    @FXML
    private ComboBox<?> cbCiudad;

    @FXML
    private ComboBox<?> cbMoneda;

    @FXML
    private TextField fieldApellido;

    @FXML
    private TextField fieldCarnet;

    @FXML
    private TextField fieldNombre;

    @FXML
    private ListView<?> lvSocios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void agregarSocio() throws IOException {
        //Obtenemos los datos a enviar
        JSONObject nuevoSocio = new JSONObject();
        nuevoSocio.put("nombre_socio", fieldNombre.getText());
        nuevoSocio.put("apellido_socio", fieldApellido.getText());
        nuevoSocio.put("cedula", fieldCarnet.getText());

        //Hacemos el post
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/socios"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(nuevoSocio.toString()))
                .build();

        String respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        System.out.println(respuesta);
        if (respuesta.equals(""))
            System.out.println("Socio agregado");
        else{
            System.out.println("No se pudo agregar el socio");
        }

    }
}

package com.darksoft.banco_taller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class CuidadController implements Initializable {

    @FXML
    private Button btnAgregarCiudad;

    @FXML
    private TextField fieldCiudad;

    @FXML
    private TextField fieldDepartamento;

    @FXML
    private TextField fieldMunicipio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void agregarCiudad() {
        //Obtenemos los datos a enviar
        JSONObject nuevaCiudad = new JSONObject();
        nuevaCiudad.put("departamento", fieldDepartamento.getText());
        nuevaCiudad.put("municipio", fieldMunicipio.getText());
        nuevaCiudad.put("ciudad", fieldCiudad.getText());

        //Hacemos el post
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/ciudades"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(nuevaCiudad.toString()))
                .build();

        String respuesta = cliente.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        System.out.println(respuesta);
        if (respuesta.equals("")) {
            System.out.println("Ciudad agregada");
            fieldDepartamento.setText("");
            fieldMunicipio.setText("");
            fieldCiudad.setText("");
        }
        else
            System.out.println("No se pudo agregar la ciudad");


    }



}

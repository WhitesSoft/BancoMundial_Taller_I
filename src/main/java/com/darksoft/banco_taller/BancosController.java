package com.darksoft.banco_taller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ResourceBundle;

public class BancosController implements Initializable {

    JSONArray listaArrayCiudades;
    JSONObject ciudadesJSON;
    String respuesta;

    @FXML
    private Button btnAgregarBanco;

    @FXML
    private ComboBox<String> cbCiudad;

    @FXML
    private TextField fieldDenominacion;

    @FXML
    private TextField fieldSigla;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        obtenerCiudades();
    }

    private void obtenerCiudades() {

        ObservableList<String> ciudades = FXCollections.observableArrayList();

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().
                uri(URI.create("http://localhost:8080/ciudades")).
                header("Content-Type", "application/json").
                GET().build();

        respuesta = cliente.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        ciudadesJSON = new JSONObject(respuesta);

        listaArrayCiudades = ciudadesJSON.optJSONObject("_embedded").optJSONArray("Ciudad");

        for (Object object : listaArrayCiudades) {
            JSONObject elemento = new JSONObject(object.toString());
            String id = elemento.getJSONObject("_links").getJSONObject("self").getString("href").substring(31);
            ciudades.add(id);
        }

        cbCiudad.setItems(ciudades);
    }

    @FXML
    private void agregarBanco() throws IOException {

        String ciudadSeleccionada = cbCiudad.getValue();

        JSONObject nuevoBanco = new JSONObject();
        nuevoBanco.put("sigla", fieldSigla.getText());
        nuevoBanco.put("denominacion", fieldDenominacion.getText());
        nuevoBanco.put("idCiudad", ciudadSeleccionada);

        //Hacemos el post
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/bancos"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(nuevoBanco.toString()))
                .build();

        HttpHeaders respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::headers).join();

        System.out.println(respuesta.firstValue("location").get());
        System.out.println("http://localhost:8080/ciudades/"+ cbCiudad.getValue());

        if (respuesta.firstValueAsLong("content-length").getAsLong() == 0){

            HttpClient relacion = HttpClient.newHttpClient();
            HttpRequest solicitud_muchos_a_muchos = HttpRequest.newBuilder()
                    .uri(URI.create(respuesta.firstValue("location").get() + "/ciudad"))
                    .header("Content-Type", "text/uri-list")
                    .PUT(BodyPublishers.ofString("http://localhost:8080/ciudades/"+ cbCiudad.getValue()))
                    .build();

            String resp = relacion.sendAsync(solicitud_muchos_a_muchos, BodyHandlers.ofString()).
                    thenApply(HttpResponse::body).join();

            System.out.println(resp);
            if (resp.equals(""))
                System.out.println("Banco agregado");
            else
                System.out.println("No se pudo agregar el banco");

        }else {
            JSONObject Error = new JSONObject(respuesta);
            System.out.println(Error.getJSONObject("cause")
                    .getString("message"));
        }

    }

}

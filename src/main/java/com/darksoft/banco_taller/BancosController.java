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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class BancosController implements Initializable {

    HashMap<String, String> valoresCuidad = new HashMap<>();
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

        String respuesta = cliente.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        JSONObject ciudadesJSON = new JSONObject(respuesta);

        System.out.println(ciudadesJSON);

        JSONArray listaArrayCiudades = ciudadesJSON.optJSONObject("_embedded").optJSONArray("Ciudad");


        for (Object object : listaArrayCiudades) {
            JSONObject elemento = new JSONObject(object.toString());
            String href = elemento.getJSONObject("_links").getJSONObject("self").getString("href");

            int id = Integer.parseInt(href.substring(href.lastIndexOf("/")+1));
            String ciudad = elemento.getString("ciudad");

            valoresCuidad.put(String.valueOf(id), ciudad);
            ciudades.add(ciudad);
        }
        System.out.println(valoresCuidad);
        cbCiudad.setItems(ciudades);
    }

    @FXML
    private void agregarBanco() throws IOException {
        //Obtenemos el valor de la ciudad
        String ciudadSeleccionada = cbCiudad.getValue();
        String llave = getSingleKeyFromValue(valoresCuidad, ciudadSeleccionada);

        //Obtenemos los datos a enviar al servidor
        JSONObject nuevoBanco = new JSONObject();
        nuevoBanco.put("sigla", fieldSigla.getText());
        nuevoBanco.put("denominacion", fieldDenominacion.getText());
        //nuevoBanco.put()

        //Hacemos el post
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/bancos"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(nuevoBanco.toString()))
                .build();

        String respuesta = cliente.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        System.out.println(respuesta);
        if (respuesta.equals("")) {
            System.out.println("Banco agregado");

            fieldSigla.setText("");
            fieldDenominacion.setText("");
        }
        else
            System.out.println("No se pudo agregar el banco");


    }

    public static <K, V> K getSingleKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}

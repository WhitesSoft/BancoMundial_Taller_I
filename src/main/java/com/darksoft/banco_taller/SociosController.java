package com.darksoft.banco_taller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class SociosController implements Initializable {

    private JSONArray listaArraySocios;
    ObservableList<String> bancos = FXCollections.observableArrayList();
    HashMap<String, String> valoresCuidad = new HashMap<>();
    HashMap<String, String> valoresBanco = new HashMap<>();

    @FXML
    private Button btnAgregarCuenta;

    @FXML
    private Button btnAgregarSocio;

    @FXML
    private ComboBox<String> cbBanco;

    @FXML
    private ComboBox<String> cbCiudad;

    @FXML
    private ComboBox<String> cbMoneda;

    @FXML
    private TextField fieldApellido;

    @FXML
    private TextField fieldCarnet;

    @FXML
    private TextField fieldNombre;

    @FXML
    private ListView<String> lvSocios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarSocios();

        //cb TipoMoneda
        ObservableList<String> tipoMoneda = FXCollections.observableArrayList();
        tipoMoneda.add("Bolivianos");
        tipoMoneda.add("Dolares");
        cbMoneda.setItems(tipoMoneda);

        obtenerCiudades();
        obtenerBancos();

    }

    private void obtenerBancos() {
        ObservableList<String> bancos = FXCollections.observableArrayList();

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().
                uri(URI.create("http://localhost:8080/bancos")).
                header("Content-Type", "application/json").
                GET().build();

        String respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        JSONObject bancosJSON = new JSONObject(respuesta);

        JSONArray listaArrayBancos = bancosJSON.optJSONObject("_embedded").optJSONArray("Banco");


        for (Object object : listaArrayBancos) {
            JSONObject elemento = new JSONObject(object.toString());
            String href = elemento.getJSONObject("_links").getJSONObject("self").getString("href");

            String id = href.substring(29);
            String banco = elemento.getString("denominacion");

            valoresBanco.put(id, banco);

            bancos.add(id);
        }
        System.out.println(valoresBanco);
        cbBanco.setItems(bancos);
    }

    private void obtenerCiudades() {

        ObservableList<String> ciudades = FXCollections.observableArrayList();

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().
                uri(URI.create("http://localhost:8080/ciudades")).
                header("Content-Type", "application/json").
                GET().build();

        String respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        JSONObject ciudadesJSON = new JSONObject(respuesta);

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
    private void agregarCuenta(){
        String bancoSeleccionado = cbBanco.getValue();
        String llaveBanco = getSingleKeyFromValue(valoresBanco, bancoSeleccionado);
        System.out.println(bancoSeleccionado);
        System.out.println(llaveBanco);

        String ciudadSeleccionada = cbCiudad.getValue();

    }

    @FXML
    private void agregarSocio() throws IOException {
        //Obtenemos los datos a enviar
        JSONObject nuevoSocio = new JSONObject();
        nuevoSocio.put("nombreSocio", fieldNombre.getText());
        nuevoSocio.put("apellidoSocio", fieldApellido.getText());
        nuevoSocio.put("cedula", Integer.parseInt(fieldCarnet.getText()));

        //Hacemos el post
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/socios"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(nuevoSocio.toString()))
                .build();

        String respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        System.out.println(respuesta);
        if (respuesta.equals("")) {
            System.out.println("Socio agregado");
            listarSocios();

            fieldNombre.setText("");
            fieldApellido.setText("");
            fieldCarnet.setText("");
        }
        else
            System.out.println("No se pudo agregar el socio");


    }

    @FXML
    private void listarSocios(){
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().
                uri(URI.create("http://localhost:8080/socios")).
                header("Conten-Type", "application/json").
                GET().build();

        String respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        JSONObject socios = new JSONObject(respuesta);

        listaArraySocios = socios.optJSONObject("_embedded").optJSONArray("Socio");

        lvSocios.getItems().clear();

        for (Object object: listaArraySocios){
            JSONObject elemento = new JSONObject(object.toString());
            lvSocios.getItems().add(elemento.getString("nombreSocio") + " " + elemento.getString("apellidoSocio"));
        }
    }

    //Metodo para obtener la llave de un valor
    public static <K, V> K getSingleKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}

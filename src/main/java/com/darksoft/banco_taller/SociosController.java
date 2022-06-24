package com.darksoft.banco_taller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.layout.VBox;
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

    @FXML
    private Button btnAgregarCuenta;

    @FXML
    private Button btnAgregarSocio;

    @FXML
    private ComboBox<String> cbBanco;


    @FXML
    private ComboBox<String> cbMoneda;

    @FXML
    private TextField fieldCarnet;
    @FXML
    private TextField fieldApellido;
    @FXML
    private TextField fieldCuenta;
    @FXML
    private TextField fieldNombre;

    @FXML
    private VBox vbAgregarCuentas;

    @FXML
    private VBox vbListarCuentas;

    @FXML
    private VBox vbMovimientos;
    @FXML
    private ListView<String> lvSocios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarSocios();
        obtenerTipoMoneda();
        obtenerBancos();
    }

    private void obtenerTipoMoneda(){
        ObservableList<String> tipoMoneda = FXCollections.observableArrayList();
        tipoMoneda.add("Bolivianos");
        tipoMoneda.add("Dolares");
        cbMoneda.setItems(tipoMoneda);
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

            bancos.add(id);
        }

        cbBanco.setItems(bancos);
    }

    @FXML
    private void agregarCuenta(){
        int id = lvSocios.getSelectionModel().getSelectedIndex();
        String idSocio = listaArraySocios.getJSONObject(id).getJSONObject("_links").getJSONObject("self")
                .getString("href");
        String bancoSeleccionado = cbBanco.getValue();

        //Obtenemos los datos a enviar
        URI banco = URI.create("http://localhost:8080/bancos/"+bancoSeleccionado);
        URI socio = URI.create(idSocio);

        JSONObject nuevaCuenta = new JSONObject();
        nuevaCuenta.put("idCuenta", Integer.parseInt(fieldCuenta.getText()));
        nuevaCuenta.put("moneda", cbMoneda.getValue());
        nuevaCuenta.put("idBanco", banco);
        nuevaCuenta.put("idSocio", socio);

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/cuentas"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(nuevaCuenta.toString()))
                .build();

        String respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString()).thenApply(HttpResponse::body).join();

        if (respuesta.equals(""))
            System.out.println("Cuenta agregada");
        else
            System.out.println("No se pudo agregar la cuenta");

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

    @FXML
    private void agregarCuentas(){
        vbAgregarCuentas.setVisible(true);
        vbListarCuentas.setVisible(false);
        vbMovimientos.setVisible(false);
    }
    @FXML
    private void listarCuentas(){
        vbListarCuentas.setVisible(true);
        vbAgregarCuentas.setVisible(false);
        vbMovimientos.setVisible(false);
    }

    @FXML
    private void movimientos(){
        vbMovimientos.setVisible(true);
        vbAgregarCuentas.setVisible(false);
        vbListarCuentas.setVisible(false);
    }



}

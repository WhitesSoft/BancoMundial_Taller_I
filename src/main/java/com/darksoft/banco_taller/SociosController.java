package com.darksoft.banco_taller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class SociosController implements Initializable {

    private JSONArray listaArraySocios;
    private JSONArray listaArrayCuentas;
    private JSONArray listaArrayMovimientos;

    @FXML
    private Button btnAgregarCuenta;
    @FXML
    private Button btnAgregarSocio;
    @FXML
    private Button btnAgregarMovimiento;
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
    private TextField fieldMovimiento;

    @FXML
    private RadioButton rbEgreso;

    @FXML
    private RadioButton rbIngreso;
    @FXML
    private VBox vbAgregarCuentas;
    @FXML
    private VBox vbListarCuentas;
    @FXML
    private VBox vbMovimientos;
    @FXML
    private ListView<String> lvCuentas;

    @FXML
    private ListView<String> lvMovimientos;
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
    private void agregarCuenta() {
        int id = lvSocios.getSelectionModel().getSelectedIndex();
        String idSocio = listaArraySocios.getJSONObject(id).getJSONObject("_links").getJSONObject("self")
                .getString("href");
        String bancoSeleccionado = cbBanco.getValue();

        //Obtenemos los datos a enviar
        URI banco = URI.create("http://localhost:8080/bancos/"+bancoSeleccionado);
        URI socio = URI.create(idSocio);

        JSONObject nuevaCuenta = new JSONObject();
        nuevaCuenta.put("idCuenta", fieldCuenta.getText());
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
    private void agregarSocio() {
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
    private void listarSocios() {
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
    private void listarCuentas() {

        int id = lvSocios.getSelectionModel().getSelectedIndex();
        String idSocio = listaArraySocios.getJSONObject(id).getJSONObject("_links").getJSONObject("self").getString("href");

        HttpClient Cliente = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(idSocio+"/listaCuentas")).
                header("Content-Type", "application/json").
                GET().build();
        String Respuesta = Cliente.sendAsync(req, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();
        JSONObject Datos = new JSONObject(Respuesta);
        listaArrayCuentas = Datos.optJSONObject("_embedded").optJSONArray("Cuenta");

        lvCuentas.getItems().clear();
        for (Object object : listaArrayCuentas) {
            JSONObject elemento = new JSONObject(object.toString());
            String href = elemento.getJSONObject("_links").getJSONObject("self").getString("href").substring(30);
            lvCuentas.getItems().add(href);
        }
    }
    @FXML
    private void listarMovimientos() {

        int idCuenta = lvCuentas.getSelectionModel().getSelectedIndex();
        String idCuentaString = listaArrayCuentas.getJSONObject(idCuenta).getJSONObject("_links").getJSONObject("self")
                .getString("href");


        HttpClient Cliente = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(idCuentaString+"/movimiento")).
                header("Content-Type", "application/json").
                GET().build();
        String Respuesta = Cliente.sendAsync(req, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();
        JSONObject Datos = new JSONObject(Respuesta);
        listaArrayMovimientos = Datos.optJSONObject("_embedded").optJSONArray("Movimiento");

        lvMovimientos.getItems().clear();
        JSONArray a = new JSONArray(listaArrayMovimientos);
        for (int i = 0; i < a.length(); i++){

            String fechaHora = a.getJSONObject(i).getString("fechaHora");
            String tipo = a.getJSONObject(i).getString("tipo");
            String monto = a.getJSONObject(i).getString("monto");

            lvMovimientos.getItems().add(fechaHora + " " + tipo + " " + monto);

        }

    }
    @FXML
    private void movimientosMenuEliminar(){
        int idCuenta = lvCuentas.getSelectionModel().getSelectedIndex();
        String idCuentaString = listaArrayCuentas.getJSONObject(idCuenta).getJSONObject("_links").getJSONObject("self")
                .getString("href");

        System.out.println(idCuentaString);

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().
                uri(URI.create(idCuentaString)).
                header("Content-Type", "application/json").
                DELETE()
                .build();

        String respuesta = cliente.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();

        System.out.println(respuesta);

        if (respuesta.equals("")){
            System.out.println("Cuenta eliminada");
            listarCuentas();
        }else
            System.out.println("No se pudo eliminar la cuenta");

    }
    @FXML
    private void agregarMovimiento(){

        //ID
        int number;
        String id = "";
        number = (int)(10000 * Math.random());
        id = "" + number;
        for(int i = id.length(); i < 4; i++){
            id = "0" + id;
        }

        //FechaHora
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String fechaHora = dtf.format(LocalDateTime.now());

        //monto
        String monto = fieldMovimiento.getText();

        //tipo
        String tipo = null;
        if (rbEgreso.isSelected())
            tipo = "Egreso";
        if (rbIngreso.isSelected())
            tipo = "Ingreso";

        //idCuenta
        int idCuenta = lvCuentas.getSelectionModel().getSelectedIndex();
        String idCuentaString = listaArrayCuentas.getJSONObject(idCuenta).getJSONObject("_links").getJSONObject("self")
                .getString("href").substring(30);


        JSONObject nuevoMoviemiento = new JSONObject();
        nuevoMoviemiento.put("idMovimiento", id);
        nuevoMoviemiento.put("fechaHora", fechaHora);
        nuevoMoviemiento.put("idCuenta", Integer.parseInt(idCuentaString));
        nuevoMoviemiento.put("monto", monto);
        nuevoMoviemiento.put("tipo", tipo);

        // Hacemos el POST
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest solicitud = HttpRequest.newBuilder().
                uri(URI.create("http://localhost:8080/movimientos")).
                header("Content-Type", "application/json").
                POST(BodyPublishers.ofString(nuevoMoviemiento.toString()))
                .build();

        HttpHeaders respuesta = client.sendAsync(solicitud, BodyHandlers.ofString())
                .thenApply(HttpResponse::headers).join();

        if (respuesta.firstValueAsLong("content-length").getAsLong() == 0){

            HttpClient relacion = HttpClient.newHttpClient();
            HttpRequest solicitud_muchos_a_muchos = HttpRequest.newBuilder().
                    uri(URI.create(respuesta.firstValue("location").get()+"/cuenta")).
                    header("Content-Type", "text/uri-list").
                    PUT(BodyPublishers.ofString("http://localhost:8080/cuentas/"+ idCuentaString))
                    .build();

            String respuestaMuchosAMuchos = relacion.sendAsync(solicitud_muchos_a_muchos, BodyHandlers.ofString())
                    .thenApply(HttpResponse::body).join();

            System.out.println(respuestaMuchosAMuchos);
            if (respuestaMuchosAMuchos.equals("")){
                System.out.println("Movimiento agregado");
                listarMovimientos();
            } else
                System.out.println("No se pudo agregar el movimiento");
        }
    }
    @FXML
    private void cuentasMenuVisible(){
        vbAgregarCuentas.setVisible(true);
        vbListarCuentas.setVisible(false);
        vbMovimientos.setVisible(false);
    }
    @FXML
    private void listarCuentasMenuVisible(){
        vbListarCuentas.setVisible(true);
        vbAgregarCuentas.setVisible(false);
        vbMovimientos.setVisible(false);

        listarCuentas();
    }

    @FXML
    private void movimientosMenuVisible(){
        vbMovimientos.setVisible(true);
        vbAgregarCuentas.setVisible(false);
        vbListarCuentas.setVisible(true);

        listarMovimientos();
    }

}

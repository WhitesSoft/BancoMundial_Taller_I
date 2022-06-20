package com.darksoft.banco_taller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
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

    }
}
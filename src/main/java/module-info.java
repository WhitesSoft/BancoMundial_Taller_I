module com.darksoft.banco_taller {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.darksoft.banco_taller to javafx.fxml;
    exports com.darksoft.banco_taller;
}
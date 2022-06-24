module com.darksoft.banco_taller {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.net.http;


    opens com.darksoft.banco_taller to javafx.fxml;
    exports com.darksoft.banco_taller;
}
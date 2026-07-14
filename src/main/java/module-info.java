module com.hotel {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    requires org.slf4j;

    exports com.hotel;

    opens com.hotel to javafx.fxml;
}
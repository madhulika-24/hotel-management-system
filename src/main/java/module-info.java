module com.hotel {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    requires org.slf4j;

    exports com.hotel;
    exports com.hotel.controller;

    opens com.hotel to javafx.fxml;
    opens com.hotel.controller to javafx.fxml;
}
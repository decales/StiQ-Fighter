module com.example.a3_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.a3_2 to javafx.fxml;
    exports com.example.a3_2;
    exports com.example.a3_2.model;
    opens com.example.a3_2.model to javafx.fxml;
    exports com.example.a3_2.view;
    opens com.example.a3_2.view to javafx.fxml;
}

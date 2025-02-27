module com.example.a3_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.a3_2 to javafx.fxml;
    exports com.example.a3_2;
    exports com.example.a3_2.model;
    opens com.example.a3_2.model to javafx.fxml;
    exports com.example.a3_2.view.game;
    opens com.example.a3_2.view.game to javafx.fxml;
    exports com.example.a3_2.view.menu;
    opens com.example.a3_2.view.menu to javafx.fxml;
}

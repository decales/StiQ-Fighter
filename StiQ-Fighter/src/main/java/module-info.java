module com.example.StiqFighter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.StiqFighter to javafx.fxml;
    exports com.example.StiqFighter;
    exports com.example.StiqFighter.model;
    opens com.example.StiqFighter.model to javafx.fxml;
    exports com.example.StiqFighter.view.game;
    opens com.example.StiqFighter.view.game to javafx.fxml;
    exports com.example.StiqFighter.view.menu;
    opens com.example.StiqFighter.view.menu to javafx.fxml;
}

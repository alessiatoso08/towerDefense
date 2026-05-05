module com.example.towerdefense {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.towerdefense to javafx.fxml;
    exports com.example.towerdefense;
}
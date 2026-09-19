module com.example.projectoop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.example.projectoop to javafx.graphics;
    opens com.example.projectoop.ui to javafx.graphics, javafx.fxml;
    opens com.example.projectoop.models to javafx.base;
    opens com.example.projectoop.services to javafx.base;
    opens com.example.projectoop.rules to javafx.base;
    opens com.example.projectoop.exceptions to javafx.base;

    exports com.example.projectoop;
    exports com.example.projectoop.ui;
}
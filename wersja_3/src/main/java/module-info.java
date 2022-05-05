module com.example.wersja_3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.desktop;
    requires java.logging;
    requires org.jetbrains.annotations;


    opens com.example.wersja_3 to javafx.fxml;
    exports com.example.wersja_3;
}
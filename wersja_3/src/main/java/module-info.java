module com.example.wersja_3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;


    opens com.example.wersja_3 to javafx.fxml;
    exports com.example.wersja_3;
}
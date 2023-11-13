module com.example.comgraph {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.comgraph to javafx.fxml;
    exports com.example.comgraph;
}
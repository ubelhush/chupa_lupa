module com.example.chupa_lupa {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.example.chupa_lupa to javafx.fxml;
    exports com.example.chupa_lupa;

    opens com.example.chupa_lupa.labaduba to javafx.fxml;
    exports com.example.chupa_lupa.labaduba;
}
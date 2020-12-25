module lexicon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires lexicondb;
    requires javaxt;
    requires java.desktop;
    requires javafx.swing;
    requires com.fasterxml.jackson.annotation;

    opens ch.azure.aurore.lexicon to javafx.fxml;
    exports ch.azure.aurore.lexicon to javafx.graphics, com.fasterxml.jackson.databind, javaxt;
}
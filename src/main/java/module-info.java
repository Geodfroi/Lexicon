module Lexicon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires JavaExt;

    opens ch.azure.aurore.lexicon to javafx.fxml;
    opens ch.azure.aurore.lexicon.data to javafx.base;
    exports ch.azure.aurore.lexicon to javafx.graphics;
}
module Lexicon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires LexiconDB;
    requires JavaExt;

    opens ch.azure.aurore.lexicon to javafx.fxml;
    exports ch.azure.aurore.lexicon to javafx.graphics;
}
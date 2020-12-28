package ch.azure.aurore.lexicon;

import ch.azure.aurore.javaxt.fxml.AppState;
import ch.azure.aurore.javaxt.fxml.FXApplication;
import javafx.fxml.FXMLLoader;

/**
 * JavaFX App
 */
public class App extends FXApplication {


    public static void main(String[] args) {
        launch();
    }

    @Override
    protected FXMLLoader getLoader(String s) {
        return new FXMLLoader(App.class.getResource("/ch/azure/aurore/lexicon/Main.fxml"));
    }

    @Override
    protected void start() {
       switchScene("Main");
    }

    @Override
    protected void quit() {
    }

    @Override
    protected Class<? extends AppState> getStateType() {
        return LexiconState.class;
    }
}
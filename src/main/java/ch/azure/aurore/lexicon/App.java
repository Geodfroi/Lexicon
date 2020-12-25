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
    protected FXMLLoader getLoader() {
        return new FXMLLoader(App.class.getResource("Main.fxml"));
    }

    @Override
    protected Class<? extends AppState> getStateType() {
        return LexiconState.class;
    }
}
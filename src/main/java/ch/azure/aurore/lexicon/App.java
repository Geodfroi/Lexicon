package ch.azure.aurore.lexicon;

import JavaExt.IO.Settings;
import ch.azure.aurore.lexicon.data.DataAccess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static final String MAIN_FXML_FILENAME = "Main.fxml";
    public static final String FILE_CURRENT_PROPERTY = "currentFile";
    public static final String FILES_LIST_PROPERTY = "filesList";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(MAIN_FXML_FILENAME));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Lexicon");

        String path = retrievePath();
        if (path != null && DataAccess.getInstance().open(path)){
            MainController controller = fxmlLoader.getController();
            controller.reloadEntries();
        }

        stage.show();
    }

    private String retrievePath() {
        String currentDatabase = Settings.getInstance().get(FILE_CURRENT_PROPERTY);
        if (currentDatabase != null) {
            return Settings.getInstance().getMapValue(FILES_LIST_PROPERTY, currentDatabase);
        }
        return null;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DataAccess.getInstance().close();
    }
}
package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexicon.data.DataAccess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

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
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Lexicon");

        String path = retrievePath();
        if (path != null && DataAccess.getInstance().open(path)){
            MainController controller = fxmlLoader.getController();
            controller.reloadEntries();
            controller.showEntriesList();
        }

        stage.show();
    }

    private String retrievePath() {
        Optional<String> currentDatabase = LocalSave.getStr(FILE_CURRENT_PROPERTY);
        if (currentDatabase.isPresent()) {
            return LocalSave.getMapValue(FILES_LIST_PROPERTY, currentDatabase.get());
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
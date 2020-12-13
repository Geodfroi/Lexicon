package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.IO.API.Settings;
import ch.azure.aurore.IO.exceptions.MissingSettingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final String APP_NAME = "Lexicon";
    private static final String APP_ICON = "file:icon.png";
    private static final String WINDOW_SIZE = "windowSize";

    private static App instance;

    private MainController mainController;
    private Scene scene;
    private Stage stage;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        instance = this;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml"));
        List<Integer> size = getSize();

        scene = new Scene(fxmlLoader.load(), size.get(0), size.get(1));
        stage.setScene(scene);
        stage.setTitle(APP_NAME);

        stage.getIcons().add(new Image(APP_ICON));

        mainController = fxmlLoader.getController();
        mainController.start();

        stage.show();
        stage.widthProperty().addListener((observableValue, number, t1) ->
                LocalSave.getInstance().setDoubles(WINDOW_SIZE, t1.doubleValue(), stage.getHeight()));
        stage.heightProperty().addListener((observableValue, number, t1) ->
                LocalSave.getInstance().setDoubles(WINDOW_SIZE, stage.getWidth(), t1.doubleValue()));
    }

    private List<Integer> getSize() {

        Optional<List<Integer>> size = LocalSave.getInstance().getIntegers(WINDOW_SIZE);
        if (size.isPresent())
            return size.get();

        size = Settings.getInstance().getIntegers(WINDOW_SIZE);
        if (size.isEmpty())
            throw new MissingSettingException(WINDOW_SIZE);
        else
            return size.get();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        mainController.quit();
        super.stop();
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }
}
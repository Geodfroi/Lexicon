package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.IO.API.Settings;
import ch.azure.aurore.IO.exceptions.MissingSettingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final String APP_NAME = "Lexicon";
    private static final String WINDOW_SIZE = "windowSize";

    private static App instance;

    private MainController mainController;
    private Stage stage;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml"));
        List<Integer> size = getSize();

        Scene scene = new Scene(fxmlLoader.load(), size.get(0), size.get(1));
        stage.setScene(scene);
        stage.setTitle(APP_NAME);

        mainController = fxmlLoader.getController();
        mainController.reloadEntries();

        stage.show();
    //    stage.setMaximized(false);
       // stage.setFullScreen(true);
        stage.widthProperty().addListener((observableValue, number, t1) -> {
            ArrayList<Double> size1 = new ArrayList<>(Arrays.asList(t1.doubleValue(), stage.getHeight()));
            LocalSave.getInstance().setDoubles(WINDOW_SIZE, size1);
        });
        stage.heightProperty().addListener((observableValue, number, t1) -> {
            var size12 = new ArrayList<>(Arrays.asList(stage.getWidth(), t1.doubleValue()));
            LocalSave.getInstance().setDoubles(WINDOW_SIZE, size12);
        });
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

    Stage getStage() {
        return stage;
    }
}
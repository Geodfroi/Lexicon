package ch.azure.aurore.lexicon;

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

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(MAIN_FXML_FILENAME));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Lexicon");

        stage.show();
    }

//
//    public static final String DATABASE_LOCATION_PROPERTY = "DatabaseLocation";
//
//    @Override
//    public void start(Stage stage) throws IOException {
//
//        String databasePath = Settings.getInstance().get(DATABASE_LOCATION_PROPERTY);
//        MainController controller = fxmlLoader.getController();
//
//        if (databasePath!= null && DataAccess.getInstance().open(databasePath)){
//            controller.reloadItems();
//        }
//        else{
//            selectAppFolder(stage, controller);
//        }
//    }
//
//    private void selectAppFolder(Stage stage, MainController controller) {
//        if (!DataAccess.getInstance().isOpened()) {
//            DirectoryChooser dialog = new DirectoryChooser();
//            dialog.setTitle("Select Database Folder");
//            File file = dialog.showDialog(stage);
//            if (file != null){
//                String databasePath = file.getAbsolutePath();
//                System.out.println(databasePath);
//
//                Settings.getInstance().set(DATABASE_LOCATION_PROPERTY, databasePath);
//                if (DataAccess.getInstance().open(databasePath))
//                    controller.reloadItems();
//            }
//        }
//    }
//
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DataAccess.getInstance().close();
    }
}


//public class Main extends Application {
//
//    public static final String DATABASE_CURRENT_PROPERTY = "CurrentFile";
//    public static final String DATABASE_LIST_PROPERTY = "FilesLocation";
//
//    @Override
//    public void start(Stage primaryStage) throws Exception{
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFX.fxml"));
//        Parent root = loader.load();
//        primaryStage.setTitle("Lexicon");
//        primaryStage.setScene(new Scene(root, 600, 500));
//
//        String databasePath = Settings.getInstance().get(DATABASE_CURRENT_PROPERTY);
//        if (databasePath!= null){
//            if (DataAccess.getInstance().open(databasePath)){
//                MainController controller = loader.getController();
//                controller.reloadEntries();
//            }
//        }
//
//        primaryStage.show();
//    }
//
//    public static String GetCurrentDatabasePath(){
//        String databasePath = Settings.getInstance().get(DATABASE_CURRENT_PROPERTY);
//        if (databasePath!= null){
//
//        }
//        return null;
//    }

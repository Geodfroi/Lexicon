package ch.azure.aurore.lexicon;

import JavaExt.IO.Settings;
import ch.azure.aurore.lexicon.data.DataAccess;
import ch.azure.aurore.lexicon.data.EntryContent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML
    public BorderPane root;
    @FXML
    public ListView<EntryContent> entriesListView;

    @FXML
    public Menu fileMenu;

    private ObservableList<EntryContent> observableList;

    @FXML
    public void closeApplication()
    {
        Platform.exit();
    }
    @FXML
    public void selectDatabase() {

        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(root.getScene().getWindow());

        if (file != null){
            String databasePath = file.getAbsolutePath();
            String databaseName = file.getName();

            if (DataAccess.getInstance().open(databasePath)){
                reloadEntries();

                Settings.getInstance().set(App.FILE_CURRENT_PROPERTY, databaseName);
                Settings.getInstance().setMapValue(App.FILES_LIST_PROPERTY, databaseName, databasePath);

                System.out.println("Database selected: " + databasePath);
            }
        }
    }

    public void reloadEntries() {
        System.out.println("reload entries");
        List<EntryContent> list = DataAccess.getInstance().QueryEntries();

        observableList = FXCollections.observableList(list);
        entriesListView.setItems(observableList);
    }
}

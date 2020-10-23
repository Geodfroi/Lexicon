package ch.azure.aurore.lexicon;

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
    public BorderPane borderPane;
    @FXML
    public ListView entriesListView;

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
        File file = dialog.showOpenDialog(borderPane.getScene().getWindow());
        throw new UnsupportedOperationException("Not implemented yet: selectDatabase");

        //        if (file != null){
//            String databasePath = file.getAbsolutePath();
//            String databaseName = file.getName();
//
//            System.out.println("Database selected: " + databasePath);
//            Settings.getInstance().set(Main.DATABASE_CURRENT_PROPERTY, databaseName);
//            Settings.getInstance().setEntry(Main.DATABASE_LIST_PROPERTY, databaseName, databasePath);
//
//            DataAccess.getInstance().open(databasePath);
//            reloadEntries();
//        }
//        else
//            System.out.println("No database selected");
    }

    public void reloadEntries() {
        System.out.println("reload entries");
        List<EntryContent> list = DataAccess.getInstance().QueryEntries();

        observableList = FXCollections.observableList(list);
        entriesListView.setItems(observableList);
    }
}

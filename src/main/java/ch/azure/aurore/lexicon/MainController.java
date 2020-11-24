package ch.azure.aurore.lexicon;

import JavaExt.IO.Settings;
import ch.azure.aurore.lexicon.data.DataAccess;
import ch.azure.aurore.lexicon.data.EntryContent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final String CURRENT_ENTRY = "currentEntry";

    @FXML
    public BorderPane root;
    @FXML
    public ListView<EntryContent> entriesListView;
    @FXML
    public TextArea textArea;
    @FXML
    public TextField labelsTextField;
    @FXML
    public TextField linksTextField;
    @FXML
    public Menu fileMenu;

    private ObservableList<EntryContent> observableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entriesListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            display(newValue);
        });
    }

    private void display(EntryContent value) {
        textArea.setText(value.getContent());
        String labelStr = JavaExt.Collections.CollectionSt.toString(value.getLabels());
        labelsTextField.setText(labelStr);
        Settings.getInstance().set(CURRENT_ENTRY, value.getId());
    }

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

        Optional<Integer> currentID = Settings.getInstance().getInt(CURRENT_ENTRY);
        if (currentID.isPresent())
        {
            Optional<EntryContent> result =  list.stream().filter(entryContent -> entryContent.getId() == currentID.get()).findAny();
            result.ifPresent(entryContent -> entriesListView.getSelectionModel().select(entryContent));
        }
    }
}

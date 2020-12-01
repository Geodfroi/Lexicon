package ch.azure.aurore.lexicon;

import JavaExt.IO.API.LocalSave;
import ch.azure.aurore.lexicon.data.DataAccess;
import ch.azure.aurore.lexicon.data.EntryContent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
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
    public VBox textVbox;
    @FXML
    public TextArea textArea;
    @FXML
    public TextFlow textFlow;
    @FXML
    public TextField labelsTextField;
    @FXML
    public TextField linksTextField;
    @FXML
    public Menu fileMenu;

    private ObservableList<EntryContent> observableList;
    private TextLoader textLoader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entriesListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> display(newValue));
        textLoader = new TextLoader(textArea, textFlow);

        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Selection");
        delete.setOnAction(actionEvent -> deleteEntry());
        menu.getItems().add(delete);

        entriesListView.setContextMenu(menu);
    }

    @FXML
    private void deleteEntry() {
        EntryContent item = entriesListView.getSelectionModel().getSelectedItem();
        if (item == null){
            System.out.println("no selection for delete");
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete selected entry ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                if (DataAccess.getInstance().removeEntry(item)){
                    observableList.remove(item);
                }
            }
        }
    }

    private void display(EntryContent value) {
        textLoader.loadDisplayText(value);
        String labelStr = JavaExt.Collections.CollectionSt.toString(value.getLabels());
        labelsTextField.setText(labelStr);
        LocalSave.set(CURRENT_ENTRY, value.getId());
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

                LocalSave.set(App.FILE_CURRENT_PROPERTY, databaseName);
                LocalSave.setMapValue(App.FILES_LIST_PROPERTY, databaseName, databasePath);

                System.out.println("Database selected: " + databasePath);
            }
        }
    }

    public void reloadEntries() {
        List<EntryContent> list = DataAccess.getInstance().queryEntries();

        observableList = FXCollections.observableList(list);
        SortedList<EntryContent> sortedList = new SortedList<>(observableList, (left, right) -> left.getFirstLabel().compareToIgnoreCase(right.getFirstLabel()));
        entriesListView.setItems(sortedList);

        Optional<Integer> currentID = LocalSave.getInt(CURRENT_ENTRY);
        if (currentID.isPresent())
        {
            Optional<EntryContent> result =  list.stream().filter(entryContent -> entryContent.getId() == currentID.get()).findAny();
            result.ifPresent(entryContent -> entriesListView.getSelectionModel().select(entryContent));
        }
    }

    @FXML
    public void createEntry()  {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Create Entry");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("NewEntry.fxml"));
        NewEntryController dialogController = new NewEntryController();
        fxmlLoader.setController(dialogController);

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create create dialog");
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            EntryContent item = dialogController.createItem();
            observableList.add(item);
            System.out.println("adding to list");
        }
    }
}
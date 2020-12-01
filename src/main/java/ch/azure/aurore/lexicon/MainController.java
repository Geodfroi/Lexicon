package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexicon.data.DataAccess;
import ch.azure.aurore.lexicon.data.EntryContent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    //region constants
    private static final String CURRENT_ENTRY = "currentEntry";
    private static final String SHOW_EMPTY_PROPERTY = "showEmptyEntries";
    //endregion

    //region fields
    @FXML
    public BorderPane root;
    @FXML
    public ListView<EntryContent> entriesListView;
    @FXML
    public VBox textVbox;
    @FXML
    public TextArea contentTextArea;
    @FXML
    public TextFlow contentTextFlow;
    @FXML
    public TextField labelsTextField;
    @FXML
    public TextField linksTextField;
    @FXML
    public Menu fileMenu;
    @FXML
    public CheckMenuItem showEmptyCheckMenu;
    @FXML
    public TextField searchTextField;
    @FXML
    public TextFlow linksTextFlow;

    private ObservableList<EntryContent> entries;
    private TextLoader textLoader;
    private LinkHandler linkHandler;
    private String filterStr = "";
    private EntryContent currentEntry;

    //endregion

    //region getters

    public EntryContent getCurrentEntry() {
        return currentEntry;
    }

    public List<EntryContent> getEntries() {
        return entries;
    }

    //endregion

    //region methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader(this);
        linkHandler = new LinkHandler(this);

        entriesListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> display(newValue));

        //entries context menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Selection");
        delete.setOnAction(actionEvent -> deleteEntry());
        menu.getItems().add(delete);

        entriesListView.setContextMenu(menu);

        // show empty entries menu
        Optional<Boolean> result = LocalSave.getBoolean(SHOW_EMPTY_PROPERTY);
        if (result.isPresent()){
            showEmptyCheckMenu.setSelected(result.get());
        }

        // search box
        searchTextField.textProperty().addListener((observableValue, s, t1) -> {
            filterStr = t1;
            showEntriesList();
        });
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
                    entries.remove(item);
                }
            }
        }
    }

    private void display(EntryContent value) {
        if (value != null){
            if (this.currentEntry != null)
                this.currentEntry.save();
            this.currentEntry = value;

            textLoader.setTextFlow();
            linkHandler.setTextFlow();
            labelsTextField.setText(value.getLabels());
            LocalSave.set(CURRENT_ENTRY, value.getId());
        }
    }

    @FXML
    public void closeApplication()
    {
        Platform.exit();
    }

    @FXML
    public void createEntry()  {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Create Entry");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("NewEntry.fxml"));
        NewEntryController dialogController = new NewEntryController(entries);
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
            entries.add(item);
            System.out.println("adding to list");
        }
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

    public void showEntriesList() {

        FilteredList<EntryContent> filteredList = new FilteredList<>(entries, new Predicate<EntryContent>() {
            @Override
            public boolean test(EntryContent entryContent) {
                if (!showEmptyCheckMenu.isSelected() && entryContent.isEmpty()) {
                    return false;
                }
                Pattern pattern = Pattern.compile("^.*" +filterStr + ".*$");
                Matcher matcher = pattern.matcher(entryContent.getLabels());
                if (!matcher.matches())
                    return false;

                return true;
            }
        });
        SortedList<EntryContent> sortedList = new SortedList<>(filteredList, (left, right) -> left.getLabels().compareToIgnoreCase(right.getLabels()));
        entriesListView.setItems(sortedList);

        Optional<Integer> currentID = LocalSave.getInt(CURRENT_ENTRY);
        if (currentID.isPresent())
        {
            Optional<EntryContent> result =  sortedList.stream().
                    filter(e -> e.getId() == currentID.get()).findAny();

            result.ifPresent(e -> entriesListView.getSelectionModel().select(e));
        }
    }

    public void reloadEntries() {
        List<EntryContent> list = DataAccess.getInstance().queryEntries();
        entries = FXCollections.observableList(list);
    }

    @FXML
    public void showEmpty() {
        LocalSave.set(SHOW_EMPTY_PROPERTY, showEmptyCheckMenu.isSelected());
        showEntriesList();
    }
    //endregion
}
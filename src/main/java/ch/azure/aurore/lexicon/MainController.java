package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    //region constants

    private static final String CURRENT_ENTRIES = "currentEntries";
    public static final String FILE_CURRENT_PROPERTY = "currentFile";
    public static final String FILES_LIST_PROPERTY = "filesList";
    private static final String SHOW_EMPTY_PROPERTY = "showEmptyEntries";

    //endregion

    //region fields

    //region FXML fields


    @FXML
    public BorderPane root;

    @FXML
    public Menu fileMenu;
    @FXML
    public MenuItem lastMenuItem;
    @FXML
    public MenuItem nextMenuItem;

    @FXML
    ListView<EntryContent> entriesListView;
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
    public CheckMenuItem showEmptyCheckMenu;
    @FXML
    public TextField searchTextField;
    @FXML
    public TextFlow linksTextFlow;

    //endregion

    private TextLoader textLoader;
    private LinkHandler linkHandler;
    private String filterStr = "";

    private String currentDatabase;
    private EntryContent currentEntry;
    private ObservableList<EntryContent> entries;

    private MenuItem selectDatabaseMenu;
    private MenuItem closeMenu;

    NavStack<EntryContent> navStack = new NavStack<>();

    //endregion

    //region getters

    EntryContent getCurrentEntry() {
        return currentEntry;
    }

    List<EntryContent> getEntries() {
        return entries;
    }

    //endregion

    //region methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader(this);
        linkHandler = new LinkHandler(this);

        entriesListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> entrySelected(newValue));

        //entries context menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Selection");
        delete.setOnAction(actionEvent -> deleteEntry());
        menu.getItems().add(delete);

        entriesListView.setContextMenu(menu);

        // show empty entries menu
        Optional<Boolean> result = LocalSave.getBoolean(SHOW_EMPTY_PROPERTY);
        result.ifPresent(aBoolean -> showEmptyCheckMenu.setSelected(aBoolean));

        // search box
        searchTextField.textProperty().addListener((observableValue, s, t1) -> {
            filterStr = t1;
            showEntriesList();
        });

        // file menus
        selectDatabaseMenu = new MenuItem("Select Database");
        selectDatabaseMenu.setOnAction(actionEvent -> openDiskDatabase());
        closeMenu = new MenuItem("Close application");
        closeMenu.setOnAction(actionEvent -> Platform.exit());

        // navigation menus
        lastMenuItem.setOnAction(actionEvent -> navStack(Direction.backward));
        nextMenuItem.setOnAction(actionEvent -> navStack(Direction.forward));
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
    private void deleteEntry() {
        EntryContent item = entriesListView.getSelectionModel().getSelectedItem();
        if (item == null){
            System.out.println("no selection for delete");
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete selected entry ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                if (LexiconDatabase.getInstance().removeEntry(item)){
                    entries.remove(item);
                }
            }
        }
    }

    private void entrySelected(EntryContent value) {
        if (value != null){
            if (this.currentEntry != null)
                this.currentEntry.save();
            this.currentEntry = value;

            textLoader.setTextFlow();
            linkHandler.setTextFlow();
            labelsTextField.setText(value.getLabels());

            LocalSave.setMapValue(CURRENT_ENTRIES, currentDatabase, value.getId());
            navStack.add(value);

            lastMenuItem.setDisable(!navStack.hasFormer());
            nextMenuItem.setDisable(!navStack.hasNext());
        }
    }

    private void navStack(Direction dir) {
        EntryContent entry = navStack.navigateStack(dir);
        entriesListView.getSelectionModel().select(entry);
    }

    public void openDiskDatabase() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(root.getScene().getWindow());

        if (file != null){
            String databasePath = file.getAbsolutePath();
            if (LexiconDatabase.getInstance().open(databasePath)){
                selectDatabase(file.getName());
                LocalSave.setMapValue(FILES_LIST_PROPERTY, currentDatabase, databasePath);
            }
            else
                reloadFileMenu();
        }
    }

    public void reloadEntries() {

        Optional<String> query = LocalSave.getStr(FILE_CURRENT_PROPERTY);
        if (query.isPresent()) {
            this.currentDatabase = query.get();
            Optional<String> pathResult = LocalSave.getMapString(FILES_LIST_PROPERTY, currentDatabase);
            if (pathResult.isPresent() && LexiconDatabase.getInstance().open(pathResult.get())){

                List<EntryContent> list = LexiconDatabase.getInstance().queryEntries();
                entries = FXCollections.observableList(list);
                showEntriesList();
            }
        }
        reloadFileMenu();
    }

    private void reloadFileMenu() {
        fileMenu.getItems().clear();
        fileMenu.getItems().add(selectDatabaseMenu);

        Set<String> set = LocalSave.getMapValues(FILES_LIST_PROPERTY).keySet();

        if (set.size() > 0 && currentDatabase != null)
        {
            fileMenu.getItems().add(new SeparatorMenuItem());

            List<CheckMenuItem> list = set.stream().
                    sorted(String::compareToIgnoreCase).
                    map(CheckMenuItem::new).
                    collect(Collectors.toList());

            list.forEach(checkMenuItem -> {
                if (currentDatabase.equals(checkMenuItem.getText()))
                    checkMenuItem.setSelected(true);

                checkMenuItem.setOnAction(actionEvent -> {
                    for (CheckMenuItem menuItem: list) {
                        menuItem.setSelected(menuItem == checkMenuItem);
                    }
                    if (!currentDatabase.equals(checkMenuItem.getText()))
                        selectDatabase(checkMenuItem.getText());
                });
                fileMenu.getItems().add(checkMenuItem);
            });

            fileMenu.getItems().add(new SeparatorMenuItem());
        }

        fileMenu.getItems().add(closeMenu);
    }

    private void selectDatabase(String name) {
        currentDatabase = name;
        LocalSave.set(FILE_CURRENT_PROPERTY, currentDatabase);
        navStack.clear();
        reloadEntries();

    }

    public void showEntriesList() {

        FilteredList<EntryContent> filteredList = new FilteredList<>(entries, entryContent -> {
            if (!showEmptyCheckMenu.isSelected() && entryContent.isEmpty()) {
                return false;
            }
            Pattern pattern = Pattern.compile("^.*" + filterStr + ".*$");
            Matcher matcher = pattern.matcher(entryContent.getLabels());
            return matcher.matches();
        });
        SortedList<EntryContent> sortedList = new SortedList<>(filteredList, (left, right) -> left.getLabels().compareToIgnoreCase(right.getLabels()));
        entriesListView.setItems(sortedList);
        if (currentDatabase != null){
            Optional<Integer> currentID = LocalSave.getMapInteger(CURRENT_ENTRIES, currentDatabase);
            if (currentID.isPresent())
            {
                Optional<EntryContent> result =  sortedList.stream().
                        filter(e -> e.getId() == currentID.get()).findAny();

                result.ifPresent(e -> entriesListView.getSelectionModel().select(e));
            }
        }
    }

    @FXML
    public void showEmpty() {
        LocalSave.set(SHOW_EMPTY_PROPERTY, showEmptyCheckMenu.isSelected());
        showEntriesList();
    }
    //endregion
}
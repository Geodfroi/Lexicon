package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseAccess {

    private static final String SELECTED_DATABASE_PROPERTY = "currentDB";
    private static final String FILES_LIST_PROPERTY = "DBPaths";

    private final MainController main;

    private Map<String, String> databases;
    private String loadedDB;
    private ObservableList<EntryContent> entries;

    public DatabaseAccess(MainController main){
        this.main = main;
        databases = LocalSave.getInstance().getMapValues(FILES_LIST_PROPERTY);
    }

    //region accessors
    public ObservableList<EntryContent> getEntries() {
        return this.entries;
    }

    public String getLoadedDB() {
        return loadedDB;
    }
    //endregion

    public void createEntry(String label) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(main.root.getScene().getWindow());
        dialog.setTitle("Create Entry");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("NewEntry.fxml"));
        NewEntryController dialogController = new NewEntryController(main, label);
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
            dialogController.createItem();
        }
    }

    public void createEntry() {
        createEntry("");
    }

    public void deleteEntry() {
        System.out.println("not implemented: delete entry");
    }

    public void deleteEntry(EntryContent entry){

        if (entry == null) {
            System.out.println("no selection for delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete ["+entry.getFirstLabel()+"] ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (LexiconDatabase.getInstance().removeEntry(entry)) {
                entries.remove(entry);
                main.getNavigation().clearEntry();
            }
        }
    }

    private Optional<String> getDatabaseStr() {
        Optional<String> name = LocalSave.getInstance().getString(SELECTED_DATABASE_PROPERTY);
        if (name.isPresent() && this.databases.containsKey(name.get()))
            return name;
        return Optional.empty();
    }

    public Map<String,String> getDBPaths() {
        return LocalSave.getInstance().getMapValues(FILES_LIST_PROPERTY);
    }

    public Optional<EntryContent> getByID(Integer id) {
        return entries.stream().
                filter(e -> id.equals(e.getId())).
                findAny();
    }

    public void loadDummyDB() {
        for (String path:LexiconDatabase.populateDummyDB()) {
            File file = new File(path);
            LocalSave.getInstance().setMapValue(FILES_LIST_PROPERTY, file.getName(), path);
        }
        main.getNavigation().clearEntry();
        main.getMenuHandler().reloadFileMenu();
    }

    public void loadDatabase(){
        Optional<String> name = getDatabaseStr();
        name.ifPresent(this::loadDatabase);
    }

    public void loadDatabase(String name) {
        if (name == null || name.equals(loadedDB)){
            return;
        }
        if (!databases.containsKey(name)){
            return;
        }
        LexiconDatabase.getInstance().open(databases.get(name));

        this.loadedDB = name;
        LocalSave.getInstance().set(SELECTED_DATABASE_PROPERTY, name);

        entries = FXCollections.observableList(LexiconDatabase.getInstance().queryEntries());
        main.getListViewHandler().displayEntries();
        main.getMenuHandler().reloadFileMenu();
        main.getNavigation().clearEntry();
        main.getNavigation().toRecordedEntry(loadedDB);
    }

    public void openDiskDatabase() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(App.getInstance().getScene().getWindow());

        if (file != null){
            String databasePath = file.getAbsolutePath();
            if (LexiconDatabase.getInstance().open(databasePath)){
                String name = file.getName();
                databases.put(name, databasePath);
                LocalSave.getInstance().setMapValue(FILES_LIST_PROPERTY, name, databasePath);
                loadDatabase(name);
            }
        }
    }

    public void clearData() {
        databases = new HashMap<>();
        entries.clear();
        loadedDB = "";
    }
}

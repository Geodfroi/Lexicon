package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class DatabaseAccess {

    private static final String SELECTED_DATABASE_PROPERTY = "currentDB";
    private static final String FILES_LIST_PROPERTY = "DBPaths";

    private final MainController main;

    private final Map<String, String> databases;
    private String loadedDB;
    private ObservableList<EntryContent> entries;

    public DatabaseAccess(MainController main){
        this.main = main;
        databases = LocalSave.getInstance().getMapValues(FILES_LIST_PROPERTY);
    }

    public ObservableList<EntryContent> getEntries() {
        return this.entries;
    }

    public String getLoadedDB() {
        return loadedDB;
    }

    public void deleteEntry(EntryContent entry){

        if (entry == null) {
            System.out.println("no selection for delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete selected entry ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (LexiconDatabase.getInstance().removeEntry(entry)) {
                entries.remove(entry);
                main.getFieldsHandler().clearDisplay();
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

    public EntryContent getByID(Integer id) {
        Optional<EntryContent> result = entries.stream().
                filter(e -> id.equals(e.getId())).
                findAny();

        return result.orElse(null);
    }

    public void loadDummyDB() {
        for (String path:LexiconDatabase.populateDummyDB()) {
            File file = new File(path);
            LocalSave.getInstance().setMapValue(FILES_LIST_PROPERTY, file.getName(), path);
        }
        main.getFieldsHandler().clearDisplay();
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
        main.getNavigation().clearNavStack();
        main.getFieldsHandler().clearDisplay();
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
}

package ch.azure.aurore.lexicon;

import ch.azure.aurore.javaxt.sqlite.wrapper.SQLite;
import ch.azure.aurore.javaxt.strings.Strings;
import ch.azure.aurore.lexiconDB.EntryContent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DatabaseAccess {
    private final MainController main;

    private String loadedDB;
    private SQLite sqLite;

    public DatabaseAccess(MainController main) {
        this.main = main;
    }

    public String getLoadedDB() {
        return loadedDB;
    }

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
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dialogController.createItem();
        }
    }

    public void createEntry() {
        createEntry("");
    }

    public void deleteEntry() {
        System.out.println("not implemented: delete entry");
    }

    public void deleteEntry(EntryContent entry) {
        if (entry == null) {
            System.out.println("no selection for delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete [" + entry.getFirstLabel() + "] ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (sqLite.removeItem(entry)) {
                main.getNavigation().clearEntry();
            }
        }
    }

    public EntryContent getByID(int id) {
        return sqLite.queryItem(EntryContent.class, id);
    }

    public void loadDummyDB() {
        for (String path : EntryContent.createDummyDB()) {
            File file = new File(path);
            LexiconState.getInstance().setDatabasePath(file.getName(), path);
        }
        main.getNavigation().clearEntry();
        main.getMenuHandler().reloadFileMenu();
    }

    public void loadDatabase() {
        String name = LexiconState.getInstance().getCurrentPathStr();
        if (!Strings.isNullOrEmpty(name))
            loadDatabase(name);
    }

    public void loadDatabase(String name) {
        if (name == null || name.equals(loadedDB)) {
            return;
        }
        String path = LexiconState.getInstance().getDatabasePath(name);
        if (path == null)
            return;

        sqLite = SQLite.connect(path);
        if (sqLite != null) {
            this.loadedDB = name;
            LexiconState.getInstance().setCurrentDB(name);
            main.getListViewHandler().displayEntries();
            main.getMenuHandler().reloadFileMenu();
            main.getNavigation().clearEntry();
            main.getNavigation().toRecordedEntry(loadedDB);
        } else
            LexiconState.getInstance().removePath(name);
    }

    public void openDiskDatabase() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(App.getInstance().getScene().getWindow());

        if (file != null) {
            String databasePath = file.getAbsolutePath();
            if ((sqLite = SQLite.connect(databasePath)) != null) {
                String name = file.getName();
                LexiconState.getInstance().setDatabasePath(name, databasePath);
                loadDatabase(name);
            }
        }
    }

    public void clearData() {
        LexiconState.getInstance().clearPaths();
        loadedDB = "";
    }

    public void close() {
        if (sqLite != null)
            sqLite.close();
    }

    public boolean updateItem(EntryContent item) {
        return sqLite.updateItem(item);
    }

    public EntryContent queryEntry(Integer id) {
        return sqLite.queryItem(EntryContent.class, id);
    }

    public List<EntryContent> queryEntries() {
        return sqLite.queryItems(EntryContent.class);
    }
}

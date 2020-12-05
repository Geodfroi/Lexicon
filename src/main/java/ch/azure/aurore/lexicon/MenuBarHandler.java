package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuBarHandler {

    private static final String SHOW_EMPTY_PROPERTY = "showEmptyEntries";

    private final MainController main;

    private final MenuItem selectDatabaseMenu;
    private final MenuItem clearDataMenu;
    private final MenuItem closeMenu;

    public MenuBarHandler(MainController main) {
        this.main=main;

        //region file menu
        selectDatabaseMenu = new MenuItem("Select Database");
        selectDatabaseMenu.setOnAction(actionEvent -> openDiskDatabase());
        clearDataMenu = new MenuItem("Reset application");
        clearDataMenu.setOnAction(actionEvent -> clearData());
        closeMenu = new MenuItem("Close application");
        closeMenu.setOnAction(actionEvent -> Platform.exit());

        //endregion

        //region edit menu
        main.createEntryMenu.setOnAction(actionEvent -> createEntry());
        main.deleteEntryMenu.setOnAction(actionEvent -> main.getListViewHandler().deleteEntry());
        //endregion

        //region display menu
        Optional<Boolean> result = LocalSave.getBoolean(SHOW_EMPTY_PROPERTY);
        result.ifPresent(aBoolean -> main.showEmptyCheckMenu.setSelected(aBoolean));
        main.showEmptyCheckMenu.setOnAction(actionEvent -> {
            LocalSave.set(SHOW_EMPTY_PROPERTY, main.showEmptyCheckMenu.isSelected());
            main.getListViewHandler().showEntriesList();
        });

        //endregion

        //region navigation menu
        main.lastMenuItem.setOnAction(actionEvent -> navStack(Direction.backward));
        main.nextMenuItem.setOnAction(actionEvent -> navStack(Direction.forward));
        //endregion
    }

    public void clearData() {
        LexiconDatabase.getInstance().close();
        LocalSave.clear();

        main.setCurrentDatabase(null);
        main.setCurrentEntry(null);

        reloadFileMenu();
        main.getEntries().clear();

        main.linksTextFlow.getChildren().clear();
        main.contentTextFlow.getChildren().clear();
        main.labelsTextField.clear();

        main.getImageHandler().setDefaultImage();
        main.getImageHandler().enableManipulateImageMenu(false);
    }

    public void createEntry()  {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(main.root.getScene().getWindow());
        dialog.setTitle("Create Entry");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("NewEntry.fxml"));
        NewEntryController dialogController = new NewEntryController(main.getEntries());
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
            main.getEntries().add(item);
            System.out.println("adding to list");
        }
    }

    public void enableLastMenu(boolean hasFormer) {
        main.lastMenuItem.setDisable(!hasFormer);
    }

    public void enableNextMenu(boolean hasNext) {
        main.nextMenuItem.setDisable(!hasNext);
    }

    public boolean IsShowEmptyEntries() {
        return main.showEmptyCheckMenu.isSelected();
    }

    private void navStack(Direction dir) {
        EntryContent entry = main.getNavStack().navigateStack(dir);
        main.entriesListView.getSelectionModel().select(entry);
    }

    private void openDiskDatabase() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(main.root.getScene().getWindow());

        if (file != null){
            String databasePath = file.getAbsolutePath();
            if (LexiconDatabase.getInstance().open(databasePath)){
                selectDatabase(file.getName());
                LocalSave.setMapValue(MainController.FILES_LIST_PROPERTY, main.getCurrentDatabase(), databasePath);
            }
            else
                reloadFileMenu();
        }
    }

    void reloadFileMenu() {
        main.fileMenu.getItems().clear();
        main.fileMenu.getItems().add(selectDatabaseMenu);
        main.fileMenu.getItems().add(clearDataMenu);

        Set<String> set = LocalSave.getMapValues(MainController.FILES_LIST_PROPERTY).keySet();

        if (set.size() > 0 && main.getCurrentDatabase() != null)
        {
            main.fileMenu.getItems().add(new SeparatorMenuItem());

            List<CheckMenuItem> list = set.stream().
                    sorted(String::compareToIgnoreCase).
                    map(CheckMenuItem::new).
                    collect(Collectors.toList());

            list.forEach(checkMenuItem -> {
                if (main.getCurrentDatabase().equals(checkMenuItem.getText()))
                    checkMenuItem.setSelected(true);

                checkMenuItem.setOnAction(actionEvent -> {
                    for (CheckMenuItem menuItem: list) {
                        menuItem.setSelected(menuItem == checkMenuItem);
                    }
                    if (!main.getCurrentDatabase().equals(checkMenuItem.getText()))
                        selectDatabase(checkMenuItem.getText());
                });
                main.fileMenu.getItems().add(checkMenuItem);
            });

            main.fileMenu.getItems().add(new SeparatorMenuItem());
        }

        main.fileMenu.getItems().add(closeMenu);
    }

    private void selectDatabase(String name) {
        main.setCurrentDatabase(name);
        LocalSave.set(MainController.FILE_CURRENT_PROPERTY, name);
        main.getNavStack().clear();
        main.reloadEntries();
    }
}

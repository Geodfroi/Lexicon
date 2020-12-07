package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.Lists.Directions;
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
    private static final String FULLSCREEN_PROPERTY = "fullscreen";

    private final MainController main;

    private final MenuItem selectDatabaseMenu;
    private final MenuItem clearDataMenu;
    private final MenuItem closeMenu;
    private final MenuItem useDummyMenu;
    private boolean canGoToFormer;
    private boolean canGoToNext;
    private boolean allowNavigation = true;

    public MenuBarHandler(MainController main) {
        this.main=main;

        //region file menu

        clearDataMenu = new MenuItem("Reset application");
        clearDataMenu.setOnAction(actionEvent -> clearData());
        closeMenu = new MenuItem("Close application");
        closeMenu.setOnAction(actionEvent -> Platform.exit());
        selectDatabaseMenu = new MenuItem("Select database");
        selectDatabaseMenu.setOnAction(actionEvent -> openDiskDatabase());
        useDummyMenu = new MenuItem("Use dummy database");
        useDummyMenu.setOnAction(actionEvent -> loadDummyDB());

        //endregion

        //region edit menu
        main.createEntryMenu.setOnAction(actionEvent -> createEntry());
        main.deleteEntryMenu.setOnAction(actionEvent -> main.getListViewHandler().deleteEntry());
        //endregion

        //region display menu
        Optional<Boolean> result = LocalSave.getInstance().getBoolean(SHOW_EMPTY_PROPERTY);
        result.ifPresent(aBoolean -> main.showEmptyCheckMenu.setSelected(aBoolean));
        main.showEmptyCheckMenu.setOnAction(actionEvent -> {
            LocalSave.getInstance().set(SHOW_EMPTY_PROPERTY, main.showEmptyCheckMenu.isSelected());
            main.getListViewHandler().refreshEntriesDisplay();
        });

        Optional<Boolean> fullscreen = LocalSave.getInstance().getBoolean(FULLSCREEN_PROPERTY);
        fullscreen.ifPresent(aBoolean -> App.getInstance().getStage().setFullScreen(aBoolean));
        main.fullScreenMenu.setOnAction(actionEvent -> {
            boolean switchedValue = !App.getInstance().getStage().isFullScreen();
            LocalSave.getInstance().set(FULLSCREEN_PROPERTY, switchedValue);
            App.getInstance().getStage().setFullScreen(switchedValue);
        });

        //endregion

        //region navigation menu
        main.lastMenuItem.setOnAction(actionEvent -> navStack(Directions.backward));
        main.nextMenuItem.setOnAction(actionEvent -> navStack(Directions.forward));

        main.root.setOnMouseClicked(mouseEvent -> {
            main.root.requestFocus();
            setAllowNavigation(true);
        });

        //endregion
    }

    public void clearData() {
        LexiconDatabase.getInstance().close();
        LocalSave.getInstance().clear();

        main.setCurrentEntry(null);

        reloadFileMenu();
        if (main.getEntries() != null)
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
        NewEntryController dialogController = new NewEntryController(main);
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
            var item = dialogController.createItem();
            item.ifPresent(entryContent -> main.entriesListView.getItems().add(entryContent));
        }
    }

    private void loadDummyDB() {
        for (String path:LexiconDatabase.populateDummyDB()) {
            File file = new File(path);
            LocalSave.getInstance().setMapValue(MainController.FILES_LIST_PROPERTY, file.getName(), path);
        }
        reloadFileMenu();
    }

    private void navStack(Directions dir) {
        EntryContent entry = main.getNavStack().navigateStack(dir);
        main.entriesListView.getSelectionModel().select(entry);
    }

    private void refreshNavigationMenu() {
        if (allowNavigation){
            main.lastMenuItem.setDisable(!canGoToFormer);
            main.nextMenuItem.setDisable(!canGoToNext);
        }
        else{
            main.lastMenuItem.setDisable(true);
            main.nextMenuItem.setDisable(true);
        }

    }

    public void setAllowNavigation(boolean val) {
        allowNavigation = val;
        refreshNavigationMenu();
    }

    public void setCanGoToFormer(boolean val) {
        canGoToFormer = val;
        refreshNavigationMenu();
    }

    public void setCanGoToNext(boolean val) {
        canGoToNext = val;
        refreshNavigationMenu();
        main.nextMenuItem.setDisable(!val);
    }

    public boolean IsShowEmptyEntries() {
        return main.showEmptyCheckMenu.isSelected();
    }

    private void openDiskDatabase() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(main.root.getScene().getWindow());

        if (file != null){
            String databasePath = file.getAbsolutePath();
            if (LexiconDatabase.getInstance().open(databasePath)){
                String name = file.getName();
                LocalSave.getInstance().setMapValue(MainController.FILES_LIST_PROPERTY, name, databasePath);
                selectDatabase(name);
            }
            else
                reloadFileMenu();
        }
    }

    void reloadFileMenu() {
        main.fileMenu.getItems().clear();
        main.fileMenu.getItems().add(selectDatabaseMenu);
        main.fileMenu.getItems().add(clearDataMenu);

        Set<String> set = LocalSave.getInstance().
                getMapValues(MainController.FILES_LIST_PROPERTY).keySet();

        if (set.size() > 0)
        {
            main.fileMenu.getItems().add(new SeparatorMenuItem());

            List<CheckMenuItem> list = set.stream().
                    sorted(String::compareToIgnoreCase).
                    map(CheckMenuItem::new).
                    collect(Collectors.toList());

            Optional<String> currentDb = main.getCurrentDB();
            for (CheckMenuItem checkMenuItem : list) {
                main.fileMenu.getItems().add(checkMenuItem);

                checkMenuItem.setOnAction(actionEvent -> {
                    for (CheckMenuItem menuItem : list) {
                        menuItem.setSelected(menuItem == checkMenuItem);
                    }
                    selectDatabase(checkMenuItem.getText());
                });

                if (currentDb.isPresent() && currentDb.get().equals(checkMenuItem.getText()))
                    checkMenuItem.setSelected(true);
            }

            main.fileMenu.getItems().add(new SeparatorMenuItem());
        }
        else{
            main.fileMenu.getItems().add(useDummyMenu);
        }

        main.fileMenu.getItems().add(closeMenu);
    }

    private void selectDatabase(String name) {
        if (main.setCurrentDB(name)){
            main.getNavStack().clear();
            main.loadDatabase();
        }
    }
}
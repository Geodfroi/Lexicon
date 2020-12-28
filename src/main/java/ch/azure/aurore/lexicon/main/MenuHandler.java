package ch.azure.aurore.lexicon.main;

import ch.azure.aurore.javaxt.collections.Directions;
import ch.azure.aurore.javaxt.strings.Strings;
import ch.azure.aurore.lexicon.App;
import ch.azure.aurore.lexicon.DatabaseAccess;
import ch.azure.aurore.lexicon.LexiconState;
import ch.azure.aurore.lexiconDB.EntryContent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuHandler {

    private final MenuItem selectDatabaseMenu;
    private final MenuItem clearDataMenu;
    private final MenuItem closeMenu;
    private final MenuItem useDummyMenu;
    private final MainController main;
    private final Menu fileMenu;

    public MenuHandler(MainController main, MenuBar menuBar) {
        this.main = main;
        this.fileMenu = getFileMenu(menuBar);

        //region file menu
        clearDataMenu = new MenuItem("Reset application");
        clearDataMenu.setOnAction(this::resetApplication);
        closeMenu = new MenuItem("Close application");
        closeMenu.setOnAction(actionEvent -> Platform.exit());
        selectDatabaseMenu = new MenuItem("Select database");
        selectDatabaseMenu.setOnAction(e -> openDatabase());
        useDummyMenu = new MenuItem("Use dummy database");
        useDummyMenu.setOnAction(actionEvent -> loadDummyDB());
        //endregion

        //region edit menu
        main.createEntryMenu.setOnAction(e -> createEntry(""));
        main.deleteEntryMenu.setOnAction(e -> deleteCurrentEntry());
        //endregion

        //region display menu
        main.showEmptyCheckMenu.setSelected(LexiconState.getInstance().isShowEmpty());
        main.showEmptyCheckMenu.setOnAction(e -> {
            LexiconState.getInstance().setShowEmpty(main.showEmptyCheckMenu.isSelected());
            main.getListViewHandler().displayEntries();
        });

        App.getInstance().getStage().setFullScreen(LexiconState.getInstance().isFullscreen());
        main.fullScreenMenu.setOnAction(actionEvent -> {
            boolean switchedValue = !App.getInstance().getStage().isFullScreen();
            LexiconState.getInstance().setFullscreen(switchedValue);
            App.getInstance().getStage().setFullScreen(switchedValue);
        });
        //endregion

        //region navigation menu
        main.lastMenuItem.setOnAction(actionEvent -> main.getNavigation().navigate(Directions.backward));
        main.nextMenuItem.setOnAction(actionEvent -> main.getNavigation().navigate(Directions.forward));
        //endregion
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

    public void deleteCurrentEntry() {
        int id = main.getNavigation().getCurrentEntry();
        if (id != 0) {
            EntryContent entry = DatabaseAccess.getInstance().queryEntry(id);
            deleteEntry(entry);
        }
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

            if (DatabaseAccess.getInstance().removeItem(entry)) {
                main.getNavigation().clearEntry();
            }
        }
    }

    public void enableNavMenus(boolean hasFormer, boolean hasNext) {
        main.lastMenuItem.setDisable(!hasFormer);
        main.nextMenuItem.setDisable(!hasNext);
    }

    private Menu getFileMenu(MenuBar parent) {
        for (Menu menu : parent.getMenus()) {
            if (menu.getId().equals("fileMenu")) {
                return menu;
            }
        }
        throw new RuntimeException("can't find [fileMenu] menu item");
    }

    private void loadDummyDB() {
        for (String path : EntryContent.createDummyDB()) {
            File file = new File(path);
            LexiconState.getInstance().setDatabasePath(file.getName(), path);
        }
        main.getNavigation().clearEntry();
        main.getMenuHandler().reloadFileMenu();
    }

    private void openDatabase() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(main.getScene().getWindow());

        if (file != null) {
            String databasePath = file.getAbsolutePath();
            String name = file.getName();
            LexiconState.getInstance().setDatabasePath(name, databasePath);
            main.loadDatabase(name);
        }
    }

    public void reloadFileMenu() {
        fileMenu.getItems().clear();
        fileMenu.getItems().add(selectDatabaseMenu);
        fileMenu.getItems().add(clearDataMenu);

        Set<String> set = LexiconState.getInstance().getDBList();

        if (set.size() > 0) {
            fileMenu.getItems().add(new SeparatorMenuItem());

            List<CheckMenuItem> list = set.stream().
                    sorted(String::compareToIgnoreCase).
                    map(CheckMenuItem::new).
                    collect(Collectors.toList());

            String loadedDB = DatabaseAccess.getInstance().getLoadedDB();
            for (CheckMenuItem checkMenuItem : list) {
                fileMenu.getItems().add(checkMenuItem);
                checkMenuItem.setOnAction(actionEvent -> {
                    for (CheckMenuItem menuItem : list) {
                        menuItem.setSelected(menuItem == checkMenuItem);
                    }
                    main.loadDatabase(checkMenuItem.getText());
                });

                if (!Strings.isNullOrEmpty(loadedDB) && loadedDB.equals(checkMenuItem.getText()))
                    checkMenuItem.setSelected(true);
            }
            fileMenu.getItems().add(new SeparatorMenuItem());
        } else {
            fileMenu.getItems().add(useDummyMenu);
        }

        fileMenu.getItems().add(closeMenu);
    }

    private void resetApplication(ActionEvent actionEvent) {
        App.getInstance().clearState();

        main.getNavigation().clearEntry();
        DatabaseAccess.getInstance().clearData();
        main.getMenuHandler().reloadFileMenu();
    }

    public boolean showEmpty() {
        return main.showEmptyCheckMenu.isSelected();
    }
}

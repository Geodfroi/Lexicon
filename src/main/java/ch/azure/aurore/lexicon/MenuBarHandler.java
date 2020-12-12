package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.collections.Directions;
import ch.azure.aurore.strings.Strings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuBarHandler {

    private static final String SHOW_EMPTY_PROPERTY = "showEmptyEntries";
    private static final String FULLSCREEN_PROPERTY = "fullscreen";

    private final MenuItem selectDatabaseMenu;
    private final MenuItem clearDataMenu;
    private final MenuItem closeMenu;
    private final MenuItem useDummyMenu;
    private final MainController main;
    private final Menu fileMenu;

    public MenuBarHandler(MainController main, MenuBar menuBar) {
        this.main=main;
        this.fileMenu = getMenu(menuBar, "fileMenu");

        //region file menu
        clearDataMenu = new MenuItem("Reset application");
        clearDataMenu.setOnAction(this::resetApplication);
        closeMenu = new MenuItem("Close application");
        closeMenu.setOnAction(actionEvent -> Platform.exit());
        selectDatabaseMenu = new MenuItem("Select database");
        selectDatabaseMenu.setOnAction(actionEvent -> main.getDatabaseAccess().openDiskDatabase());
        useDummyMenu = new MenuItem("Use dummy database");
        useDummyMenu.setOnAction(actionEvent -> main.getDatabaseAccess().loadDummyDB());
        //endregion

        //region edit menu
        main.createEntryMenu.setOnAction(actionEvent -> main.getDatabaseAccess().createEntry());
        main.deleteEntryMenu.setOnAction(actionEvent -> main.getDatabaseAccess().deleteEntry());
        //endregion

        //region display menu
        Optional<Boolean> result = LocalSave.getInstance().getBoolean(SHOW_EMPTY_PROPERTY);
        result.ifPresent(main.showEmptyCheckMenu::setSelected);
        main.showEmptyCheckMenu.setOnAction(actionEvent -> {
            LocalSave.getInstance().set(SHOW_EMPTY_PROPERTY, main.showEmptyCheckMenu.isSelected());
            main.getListViewHandler().displayEntries();
        });

//        Optional<Boolean> fullscreen = LocalSave.getInstance().getBoolean(FULLSCREEN_PROPERTY);
//        fullscreen.ifPresent(aBoolean -> App.getInstance().getStage().setFullScreen(aBoolean));
//        main.fullScreenMenu.setOnAction(actionEvent -> {
//            boolean switchedValue = !App.getInstance().getStage().isFullScreen();
//            LocalSave.getInstance().set(FULLSCREEN_PROPERTY, switchedValue);
//            App.getInstance().getStage().setFullScreen(switchedValue);
//        });
        //endregion
//
        //region navigation menu
        main.lastMenuItem.setOnAction(actionEvent -> main.getNavigation().navigate(Directions.backward));
        main.nextMenuItem.setOnAction(actionEvent -> main.getNavigation().navigate(Directions.forward));
//
//        main.root.setOnMouseClicked(mouseEvent -> {
//            main.root.requestFocus();
//            setAllowNavigation(true);
//        });
//endregion
//    }
    }

    private void resetApplication(ActionEvent actionEvent) {
        LocalSave.getInstance().clear();

        main.getNavigation().clearEntry();
        main.getDatabaseAccess().clearData();
        main.getMenuHandler().reloadFileMenu();
    }

    private Menu getMenu(MenuBar parent, String id) {
        for (Menu menu:parent.getMenus()) {
            if (menu.getId().equals(id)){
                return menu;
            }
        }
        throw new RuntimeException("can't find [" + id + "] menu item");
    }

    public void reloadFileMenu() {

        fileMenu.getItems().clear();
        fileMenu.getItems().add(selectDatabaseMenu);
        fileMenu.getItems().add(clearDataMenu);

        Set<String> set = main.getDatabaseAccess().getDBPaths().keySet();

        if (set.size() > 0)
        {
            fileMenu.getItems().add(new SeparatorMenuItem());

            List<CheckMenuItem> list = set.stream().
                    sorted(String::compareToIgnoreCase).
                    map(CheckMenuItem::new).
                    collect(Collectors.toList());

            String loadedDB = main.getDatabaseAccess().getLoadedDB();
            for (CheckMenuItem checkMenuItem : list) {
                fileMenu.getItems().add(checkMenuItem);

                checkMenuItem.setOnAction(actionEvent -> {
                    for (CheckMenuItem menuItem : list) {
                        menuItem.setSelected(menuItem == checkMenuItem);
                    }
                    main.getDatabaseAccess().loadDatabase(checkMenuItem.getText());
                });

                if (!Strings.isNullOrEmpty(loadedDB) && loadedDB.equals(checkMenuItem.getText()))
                    checkMenuItem.setSelected(true);
            }
            fileMenu.getItems().add(new SeparatorMenuItem());
        }
        else{
            fileMenu.getItems().add(useDummyMenu);
        }

        fileMenu.getItems().add(closeMenu);

    }

    public boolean hideEmptyEntries() {
        System.out.println("HideEmptyEntries not implemented");
        return false;
    }

    public void enableNavMenus(boolean hasFormer, boolean hasNext) {
        main.lastMenuItem.setDisable(!hasFormer);
        main.nextMenuItem.setDisable(!hasNext);
    }

    public boolean showEmpty() {
        return main.showEmptyCheckMenu.isSelected();
    }
}

//    public void clearData() {
//        LexiconDatabase.getInstance().close();
//        LocalSave.getInstance().clear();
//
//        main.setCurrentEntry(null);
//
//        reloadFileMenu();
//        if (main.getEntries() != null)
//            main.getEntries().clear();
//
//        main.linksTextFlow.getChildren().clear();
//        main.contentTextFlow.getChildren().clear();
//        main.labelsTextField.clear();
//
//        main.getImageHandler().setDefaultImage();
//        main.getImageHandler().enableManipulateImageMenu(false);
//    }
//

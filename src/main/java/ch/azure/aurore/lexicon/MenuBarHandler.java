package ch.azure.aurore.lexicon;

import ch.azure.aurore.javaxt.collections.Directions;
import ch.azure.aurore.javaxt.strings.Strings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuBarHandler {

    private final MenuItem selectDatabaseMenu;
    private final MenuItem clearDataMenu;
    private final MenuItem closeMenu;
    private final MenuItem useDummyMenu;
    private final MainController main;
    private final Menu fileMenu;

    public MenuBarHandler(MainController main, MenuBar menuBar) {
        this.main = main;
        this.fileMenu = getFileMenu(menuBar);

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
        main.showEmptyCheckMenu.setSelected(LexiconState.getInstance().isShowEmpty());
        main.showEmptyCheckMenu.setOnAction(actionEvent -> {
            LexiconState.getInstance().setShowEmpty( main.showEmptyCheckMenu.isSelected());
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

    private void resetApplication(ActionEvent actionEvent) {
        App.getInstance().clearState();

        main.getNavigation().clearEntry();
        main.getDatabaseAccess().clearData();
        main.getMenuHandler().reloadFileMenu();
    }

    private Menu getFileMenu(MenuBar parent) {
        for (Menu menu : parent.getMenus()) {
            if (menu.getId().equals("fileMenu")) {
                return menu;
            }
        }
        throw new RuntimeException("can't find [fileMenu] menu item");
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
        } else {
            fileMenu.getItems().add(useDummyMenu);
        }

        fileMenu.getItems().add(closeMenu);
    }

    public void enableNavMenus(boolean hasFormer, boolean hasNext) {
        main.lastMenuItem.setDisable(!hasFormer);
        main.nextMenuItem.setDisable(!hasNext);
    }

    public boolean showEmpty() {
        return main.showEmptyCheckMenu.isSelected();
    }
}

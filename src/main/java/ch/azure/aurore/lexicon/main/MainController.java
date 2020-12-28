package ch.azure.aurore.lexicon.main;

import ch.azure.aurore.javaxt.fxml.Controller;
import ch.azure.aurore.javaxt.strings.Strings;
import ch.azure.aurore.lexicon.DatabaseAccess;
import ch.azure.aurore.lexicon.LexiconState;
import ch.azure.aurore.lexiconDB.EntryContent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends Controller {

    //region fields
    //region FXML fields
    @FXML
    public MenuBar menuBar;
    @FXML
    public BorderPane root;
    @FXML
    public Menu fileMenu;
    @FXML
    public MenuItem lastMenuItem;
    @FXML
    public MenuItem nextMenuItem;
    @FXML
    public MenuItem deleteEntryMenu;
    @FXML
    public ImageView imageView;
    @FXML
    public StackPane imageStackPane;
    @FXML
    public MenuItem createEntryMenu;
    @FXML
    public MenuItem fullScreenMenu;
    @FXML
    public StackPane scrollPane;
    @FXML
    public ScrollPane textFlow_scrollPane;
    @FXML
    public ListView<EntryContent> entriesListView;
    @FXML
    public TextArea contentTextArea;
    @FXML
    public TextFlow contentTextFlow;
    @FXML
    public TextField labelsTextField;
    @FXML
    public TextArea linksTextArea;
    @FXML
    public CheckMenuItem showEmptyCheckMenu;
    @FXML
    public TextField searchTextField;
    @FXML
    public TextFlow linksTextFlow;
    //endregion

    private ListViewHandler listViewHandler;
    private MenuHandler menuHandler;

    private NavigationHandler navigationHandler;
    private FieldsHandler fieldsHandler;
    //endregion

    public ListViewHandler getListViewHandler() {
        return listViewHandler;
    }

    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public NavigationHandler getNavigation() {
        return navigationHandler;
    }

    public FieldsHandler getFieldsHandler() {
        return fieldsHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewHandler = new ListViewHandler(this, entriesListView);
        menuHandler = new MenuHandler(this, menuBar);
        navigationHandler = new NavigationHandler(this);
        fieldsHandler = new FieldsHandler(this);
    }

    public void quit() {
        fieldsHandler.recordDisplay();
        DatabaseAccess.getInstance().close();
    }

    @Override
    protected void resume() {
    }

    public void start() {
        String name = LexiconState.getInstance().getCurrentPathStr();
        if (!Strings.isNullOrEmpty(name)) {
            loadDatabase(name);
        }
    }

    public void loadDatabase(String name) {
        if (DatabaseAccess.getInstance().loadDatabase(name)) {
            listViewHandler.displayEntries();
            menuHandler.reloadFileMenu();
            navigationHandler.clearEntry();
            navigationHandler.toRecordedEntry(name);
        }
    }
}
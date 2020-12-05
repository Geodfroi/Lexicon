package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.IEntryListener;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

import javax.swing.text.View;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    //region constants

    public static final String FILE_CURRENT_PROPERTY = "currentFile";
    public static final String FILES_LIST_PROPERTY = "filesList";

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
    public MenuItem deleteEntryMenu;
    @FXML
    public ImageView imageView;
    @FXML
    public StackPane imageStackPane;
    @FXML
    public MenuItem createEntryMenu;
    @FXML
    ListView<EntryContent> entriesListView;
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

    private ImageHandler imageHandler;
    private LabelHandler labelHandler;
    private LinkHandler linkHandler;
    private ListViewHandler listViewHandler;
    private MenuBarHandler menuHandler;
    private TextLoader textLoader;

    private String currentDatabase;
    private EntryContent currentEntry;
    private ObservableList<EntryContent> entries;

    private final NavStack<EntryContent> navStack = new NavStack<>();

    //endregion

    //region accessors

    String getCurrentDatabase() {
        return currentDatabase;
    }

    EntryContent getCurrentEntry() {
        return currentEntry;
    }

    ObservableList<EntryContent> getEntries() {
        return entries;
    }

    ImageHandler getImageHandler() {
        return imageHandler;
    }

    LinkHandler getLinksHandler() {
        return linkHandler;
    }

    ListViewHandler getListViewHandler() {
        return listViewHandler;
    }

    MenuBarHandler getMenuHandler() {
        return menuHandler;
    }

    NavStack<EntryContent> getNavStack() {
        return navStack;
    }

    TextLoader getTextLoader() {
        return textLoader;
    }

    void setCurrentDatabase(String str) {
        this.currentDatabase = str;
    }

    void setCurrentEntry(EntryContent value) {
        this.currentEntry = value;
    }

    //endregion

    //region methods

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader(this);
        linkHandler = new LinkHandler(this);
        labelHandler = new LabelHandler(this);
        listViewHandler = new ListViewHandler(this);
        menuHandler = new MenuBarHandler(this);
        imageHandler = new ImageHandler(this);
    }

    public void reloadEntries() {

        Optional<String> query = LocalSave.getStr(FILE_CURRENT_PROPERTY);
        if (query.isPresent()) {
            this.currentDatabase = query.get();
            Optional<String> pathResult = LocalSave.getMapString(FILES_LIST_PROPERTY, currentDatabase);
            if (pathResult.isPresent() && LexiconDatabase.getInstance().open(pathResult.get())){

                List<EntryContent> list = LexiconDatabase.getInstance().queryEntries();
                entries = FXCollections.observableList(list);
                listViewHandler.showEntriesList();
            }
        }
        getMenuHandler().reloadFileMenu();
    }

    public void quit() {
        if (currentEntry != null)
            currentEntry.saveEntry();

        LexiconDatabase.getInstance().close();
    }

    //endregion
}
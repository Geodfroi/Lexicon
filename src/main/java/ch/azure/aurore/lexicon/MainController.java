package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.Lists.NavStack;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    //region constants

    private static final String SELECTED_DB_PROPERTY = "currentFile";
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
    public MenuItem fullScreenMenu;
    @FXML
    public StackPane scrollPane;
    @FXML
    public ScrollPane textFlow_scrollPane;
    @FXML
    ListView<EntryContent> entriesListView;
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

    private ImageHandler imageHandler;
    private LinkHandler linkHandler;
    private ListViewHandler listViewHandler;
    private MenuBarHandler menuHandler;
    private TextLoader textLoader;

    private EntryContent currentEntry;
    private ObservableList<EntryContent> entries;

    private final NavStack<EntryContent> navStack = new NavStack<>();

    //endregion

    //region accessors

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

    void setCurrentEntry(EntryContent value) {
        this.currentEntry = value;
    }

    //endregion

    //region methods

    public EntryContent getByID(Integer id) {
        Optional<EntryContent> result = entries.stream().
                filter(e -> id.equals(e.getId())).
                findAny();

        return result.orElse(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader(this);
        linkHandler = new LinkHandler(this);
        new LabelHandler(this);
        listViewHandler = new ListViewHandler(this);
        menuHandler = new MenuBarHandler(this);
        imageHandler = new ImageHandler(this);
    }

    public void loadDatabase() {

        Optional<String> db = getCurrentDB();
        if (db.isPresent()) {

            Optional<String> pathResult = LocalSave.getInstance().getMapString(FILES_LIST_PROPERTY, db.get());
            if (pathResult.isPresent() && Files.exists(Path.of(pathResult.get()))) {
                if (LexiconDatabase.getInstance().open(pathResult.get())) {
                    entries = FXCollections.observableList(LexiconDatabase.getInstance().queryEntries());
                    listViewHandler.refreshEntriesDisplay();
                }
            }
        }

        getMenuHandler().reloadFileMenu();
    }

    public void quit() {
        LexiconDatabase.getInstance().close();
    }

    public Optional<String> getCurrentDB() {
        return LocalSave.getInstance().getString(SELECTED_DB_PROPERTY);
    }

    public boolean setCurrentDB(String name) {
        Optional<String> current = LocalSave.getInstance().getString(SELECTED_DB_PROPERTY);
        if (current.isPresent() && current.get().equals(name)){
            return false;
        }
        LocalSave.getInstance().set(SELECTED_DB_PROPERTY, name);
        return true;
    }

    //endregion
}
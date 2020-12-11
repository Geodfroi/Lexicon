package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    //region fields
    //region FXML fields
    @FXML
    private MenuBar menuBar;
    @FXML
    private BorderPane root;
    @FXML
    private Menu fileMenu;
    @FXML
    private MenuItem lastMenuItem;
    @FXML
    private MenuItem nextMenuItem;
    @FXML
    private MenuItem deleteEntryMenu;
    @FXML
    private ImageView imageView;
    @FXML
    private  StackPane imageStackPane;
    @FXML
    private  MenuItem createEntryMenu;
    @FXML
    private  MenuItem fullScreenMenu;
    @FXML
    private StackPane scrollPane;
    @FXML
    private ScrollPane textFlow_scrollPane;
    @FXML
    private ListView<EntryContent> entriesListView;
    @FXML
    private TextArea contentTextArea;
    @FXML
    private TextFlow contentTextFlow;
    @FXML
    private TextField labelsTextField;
    @FXML
    private TextArea linksTextArea;
    @FXML
    private CheckMenuItem showEmptyCheckMenu;
    @FXML
    private TextField searchTextField;
    @FXML
    private TextFlow linksTextFlow;
    //endregion

    private ListViewHandler listViewHandler;
    private MenuBarHandler menuHandler;
    private DatabaseAccess databaseAccess;

    private NavigationHandler navigationHandler;
    private FieldsHandler fieldsHandler;
    //endregion

    //region accessors
    public ListViewHandler getListViewHandler() {
        return listViewHandler;
    }

    public MenuBarHandler getMenuHandler() {
        return menuHandler;
    }

    public NavigationHandler getNavigation() {
        return navigationHandler;
    }

    public FieldsHandler getFieldsHandler() {
        return fieldsHandler;
    }

    public DatabaseAccess getDatabaseAccess() {
        return databaseAccess;
    }
    //endregion

    //region methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewHandler = new ListViewHandler(this, entriesListView);
        menuHandler = new MenuBarHandler(this, menuBar);
        databaseAccess = new DatabaseAccess(this);
        navigationHandler = new NavigationHandler(this);
        fieldsHandler = new FieldsHandler(this,
                labelsTextField,
                linksTextArea,
                contentTextArea,
                imageView);
    }

    public void quit() {
        LexiconDatabase.getInstance().close();
    }

    public void start() {
        databaseAccess.loadDatabase();
        menuHandler.reloadFileMenu();
    }
}

//    public EntryContent getCurrentEntry() {
//        if (entries == null)
//            return null;
//
//        Optional<String> db = getCurrentDB();
//        if (db.isPresent()){
//            Optional<Integer> val = LocalSave.getInstance().
//                    getMapInteger(SELECTED_ENTRIES, db.get());
//            if (val.isPresent()) {
//                return getByID(val.get());
//            }
//        }
//        return null;
//    }

//    public boolean setCurrentEntry(EntryContent value) {
//        Optional<String> db = getCurrentDB();
//        if (db.isPresent()) {
//            if (value == null){
//                LocalSave.getInstance().setMapValue(SELECTED_ENTRIES, db.get(), value.getId());
//            }
//            else{
//                LocalSave.getInstance().removeMapValue(SELECTED_ENTRIES, db.get());
//            }
//            return true;
//        }
//        return false;
//    }

//endregion